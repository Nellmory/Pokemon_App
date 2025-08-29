package ru.pokemon_app.data.local.datasource

import jakarta.inject.Inject
import ru.pokemon_app.data.local.dao.PokemonDao
import ru.pokemon_app.data.local.entity.PokemonCacheEntity
import ru.pokemon_app.common.Result

class LocalDataSource @Inject constructor(
    private val pokemonDao: PokemonDao
) {
    suspend fun filterPokemons(
        type: String?,
        minHp: Int?,
        minAttack: Int?,
        minDefense: Int?,
        orderBy: String?
    ): Result<List<PokemonCacheEntity>> {
        return try {
            val result = pokemonDao.filterPokemons(type, minHp, minAttack, minDefense, orderBy)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun getPokemons(offset: Int, limit: Int): Result<List<PokemonCacheEntity>> {
        return try {
            Result.Success(pokemonDao.getPokemons(offset, limit))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun getPokemonById(id: Int): Result<PokemonCacheEntity?> {
        return try {
            Result.Success(pokemonDao.getPokemonById(id))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun searchPokemons(query: String): Result<List<PokemonCacheEntity>> {
        return try {
            Result.Success(pokemonDao.searchPokemons(query))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun insertPokemon(pokemon: PokemonCacheEntity): Result<Unit> {
        return try {
            pokemonDao.insertPokemon(pokemon)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun clearOldCache(timestamp: Long): Result<Unit> {
        return try {
            pokemonDao.clearOldCache(timestamp)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}