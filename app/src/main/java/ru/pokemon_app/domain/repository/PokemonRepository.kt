package ru.pokemon_app.domain.repository

import ru.pokemon_app.domain.model.Pokemon
import ru.pokemon_app.common.Result
import ru.pokemon_app.domain.model.PokemonListResponse

interface PokemonRepository {
    suspend fun getPokemons(page: Int, query: String?): Result<PokemonListResponse>
    suspend fun getPokemonDetails(id: Int): Result<Pokemon>
    suspend fun getFilteredPokemons(
        type: String?,
        minHp: Int?,
        minAttack: Int?,
        minDefense: Int?,
        orderBy: String?
    ): Result<PokemonListResponse>
    suspend fun clearOldCache(): Result<Unit>
}