package ru.pokemon_app.domain.repository

import ru.pokemon_app.domain.model.Pokemon
import ru.pokemon_app.domain.model.PokemonListResponse

interface PokemonRepository {
    suspend fun getPokemons(page: Int, query: String?): PokemonListResponse?
    suspend fun getPokemonDetails(id: Int): Pokemon?
    suspend fun getFilteredPokemons(
        type: String?,
        minHp: Int?,
        minAttack: Int?,
        minDefense: Int?,
        orderBy: String?
    ): PokemonListResponse?
    suspend fun clearOldCache()
}