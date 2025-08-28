package ru.pokemon_app.data.remote.repository

import android.content.Context
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.pokemon_app.data.local.database.AppDatabase
import ru.pokemon_app.data.local.entity.PokemonCacheEntity
import ru.pokemon_app.data.remote.datasource.RetrofitClient
import ru.pokemon_app.domain.model.Pokemon
import ru.pokemon_app.domain.model.PokemonListResponse
import javax.inject.Inject
import ru.pokemon_app.data.repository.PokemonRepository

class PokemonRepositoryImpl @Inject constructor(
    private val context: Context
) : PokemonRepository {
    private val apiService = RetrofitClient.api
    private val database = AppDatabase.getDatabase(context)
    private val pokemonDao = database.pokemonDao()
    private val json = Json { ignoreUnknownKeys = true }
    private val cacheTimeout = 24 * 60 * 60 * 1000
    private val typeCache = mutableMapOf<Int, String>()

    override suspend fun getPokemons(page: Int, query: String?): PokemonListResponse? {
        return try {
            val offset = (page - 1) * 20
            val response = apiService.getPokemons(offset = offset, search = query)

            val enrichedResults = response.results.map { item ->
                val id = extractIdFromUrl(item.url)
                val type = getPokemonType(id)
                item.copy(type = type)
            }

            response.copy(results = enrichedResults)
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Error getting pokemons: ${e.message}")
            null
        }
    }

    private fun extractIdFromUrl(url: String): Int {
        return url.trimEnd('/').split('/').last().toInt()
    }

    override suspend fun getPokemonDetails(id: Int): Pokemon? {
        return try {
            val pokemon = apiService.getPokemonById(id)
            saveToCache(pokemon)
            pokemon.types.firstOrNull()?.type?.name?.let { type ->
                typeCache[id] = type
            }
            pokemon
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Error getting pokemon $id: ${e.message}")
            getFromCache(id)
        }
    }

    override suspend fun getPokemonDetailsByName(name: String): Pokemon? {
        return try {
            val pokemon = apiService.getPokemonByName(name)
            saveToCache(pokemon)
            pokemon
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Error getting pokemon $name: ${e.message}")
            null
        }
    }

    override suspend fun getPokemonType(pokemonId: Int): String? {
        typeCache[pokemonId]?.let { return it }

        val cachedPokemon = getFromCache(pokemonId)
        cachedPokemon?.types?.firstOrNull()?.type?.name?.let { type ->
            typeCache[pokemonId] = type
            return type
        }

        return try {
            val pokemon = apiService.getPokemonById(pokemonId)
            val type = pokemon.types.firstOrNull()?.type?.name
            type?.let { typeCache[pokemonId] = it }
            type
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Error getting pokemon type: ${e.message}")
            null
        }
    }

    override suspend fun clearOldCache() {
        val cutoffTime = System.currentTimeMillis() - cacheTimeout
        pokemonDao.clearOldCache(cutoffTime)
    }

    private suspend fun getFromCache(id: Int): Pokemon? {
        return try {
            val cached = pokemonDao.getPokemonById(id)
            cached?.let { convertFromCache(it) }
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Error getting from cache: ${e.message}")
            null
        }
    }

    private suspend fun saveToCache(pokemon: Pokemon) {
        try {
            val cacheEntity = PokemonCacheEntity(
                id = pokemon.id,
                name = pokemon.name,
                height = pokemon.height,
                weight = pokemon.weight,
                baseExperience = pokemon.baseExperience,
                types = json.encodeToString(pokemon.types),
                stats = json.encodeToString(pokemon.stats),
                sprites = json.encodeToString(pokemon.sprites),
                abilities = json.encodeToString(pokemon.abilities),
                timestamp = System.currentTimeMillis()
            )
            pokemonDao.insertPokemon(cacheEntity)
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Error saving to cache: ${e.message}")
        }
    }

    private fun convertFromCache(cached: PokemonCacheEntity): Pokemon {
        return Pokemon(
            id = cached.id,
            name = cached.name,
            height = cached.height,
            weight = cached.weight,
            baseExperience = cached.baseExperience,
            types = json.decodeFromString(cached.types),
            stats = json.decodeFromString(cached.stats),
            sprites = json.decodeFromString(cached.sprites),
            abilities = json.decodeFromString(cached.abilities)
        )
    }
}