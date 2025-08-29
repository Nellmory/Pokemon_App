package ru.pokemon_app.data.repository

import android.content.Context
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.pokemon_app.data.local.database.AppDatabase
import ru.pokemon_app.data.local.entity.PokemonCacheEntity
import ru.pokemon_app.data.remote.datasource.RetrofitClient
import ru.pokemon_app.domain.model.*
import javax.inject.Inject
import ru.pokemon_app.domain.repository.PokemonRepository

class PokemonRepositoryImpl @Inject constructor(
    context: Context
) : PokemonRepository {

    private val apiService = RetrofitClient.api
    private val database = AppDatabase.getDatabase(context)
    private val pokemonDao = database.pokemonDao()
    private val json = Json { ignoreUnknownKeys = true }
    private val cacheTimeout = 24 * 60 * 60 * 1000 // 24h

    override suspend fun getPokemons(page: Int, query: String?): PokemonListResponse? {
        val offset = (page - 1) * 20

        return try {
            if (query.isNullOrEmpty()) {
                val response = apiService.getPokemons(offset = offset, limit = 20)

                val enrichedResults = response.results.map { item ->
                    val id = extractIdFromUrl(item.url)
                    val pokemon = apiService.getPokemonById(id)
                    saveToCache(pokemon)
                    val typeName = pokemon.types.firstOrNull()?.type?.name
                    item.copy(type = typeName)
                }

                response.copy(results = enrichedResults)
            } else {
                val cached = pokemonDao.searchPokemons(query).map { convertFromCache(it) }
                if (cached.isNotEmpty()) {
                    PokemonListResponse(
                        count = cached.size,
                        next = null,
                        previous = null,
                        results = cached.map {
                            val typeName = it.types.firstOrNull()?.type?.name
                            PokemonListItem(
                                name = it.name,
                                url = "offline/${it.id}",
                                type = typeName
                            )
                        }
                    )
                } else null
            }
        } catch (e: Exception) {
            val cached = if (query.isNullOrEmpty()) {
                pokemonDao.getPokemons(offset, 20).map { convertFromCache(it) }
            } else {
                pokemonDao.searchPokemons(query).map { convertFromCache(it) }
            }

            if (cached.isNotEmpty()) {
                PokemonListResponse(
                    count = cached.size,
                    next = null,
                    previous = null,
                    results = cached.map {
                        val typeName = it.types.firstOrNull()?.type?.name
                        PokemonListItem(
                            name = it.name,
                            url = "offline/${it.id}",
                            type = typeName
                        )
                    }
                )
            } else null
        }
    }

    override suspend fun getFilteredPokemons(
        type: String?,
        minHp: Int?,
        minAttack: Int?,
        minDefense: Int?,
        orderBy: String?
    ): PokemonListResponse? {
        return try {
            val cached = pokemonDao.filterPokemons(type, minHp, minAttack, minDefense, orderBy)
                .map { convertFromCache(it) }

            if (cached.isNotEmpty()) {
                PokemonListResponse(
                    count = cached.size,
                    next = null,
                    previous = null,
                    results = cached.map {
                        val typeName = it.types.firstOrNull()?.type?.name
                        PokemonListItem(
                            name = it.name,
                            url = "offline/${it.id}",
                            type = typeName
                        )
                    }
                )
            } else null
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Ошибка фильтрации: ${e.message}")
            null
        }
    }

    override suspend fun getPokemonDetails(id: Int): Pokemon? {
        return try {
            val pokemon = apiService.getPokemonById(id)
            saveToCache(pokemon)
            pokemon
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Ошибка деталей $id: ${e.message}")
            getFromCache(id)
        }
    }

    override suspend fun clearOldCache() {
        val cutoffTime = System.currentTimeMillis() - cacheTimeout
        pokemonDao.clearOldCache(cutoffTime)
    }

    private fun extractIdFromUrl(url: String): Int =
        url.trimEnd('/').split('/').last().toInt()

    private suspend fun getFromCache(id: Int): Pokemon? {
        val cached = pokemonDao.getPokemonById(id)
        return cached?.let { convertFromCache(it) }
    }

    private suspend fun saveToCache(pokemon: Pokemon) {
        try {
            val hp = pokemon.stats.firstOrNull { it.stat.name == "hp" }?.baseStat ?: 0
            val attack = pokemon.stats.firstOrNull { it.stat.name == "attack" }?.baseStat ?: 0
            val defense = pokemon.stats.firstOrNull { it.stat.name == "defense" }?.baseStat ?: 0

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
                hp = hp,
                attack = attack,
                defense = defense,
                timestamp = System.currentTimeMillis()
            )
            pokemonDao.insertPokemon(cacheEntity)
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Ошибка сохранения в кеш: ${e.message}")
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