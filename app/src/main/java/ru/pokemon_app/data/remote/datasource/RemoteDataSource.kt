package ru.pokemon_app.data.remote.datasource

import jakarta.inject.Inject
import ru.pokemon_app.data.remote.api.ApiService
import ru.pokemon_app.data.remote.model.PokemonDto
import ru.pokemon_app.data.remote.model.PokemonListResponseDto
import ru.pokemon_app.common.Result

class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getPokemons(offset: Int, limit: Int = 20): Result<PokemonListResponseDto> {
        return try {
            Result.Success(apiService.getPokemons(offset, limit))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun getPokemonById(id: Int): Result<PokemonDto> {
        return try {
            Result.Success(apiService.getPokemonById(id))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}