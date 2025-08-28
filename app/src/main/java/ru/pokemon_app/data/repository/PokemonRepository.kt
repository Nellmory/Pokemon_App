package ru.pokemon_app.data.repository

import ru.pokemon_app.domain.model.Pokemon
import ru.pokemon_app.domain.model.PokemonListResponse

interface PokemonRepository {
    suspend fun getPokemons(page: Int, query: String?): PokemonListResponse?
    suspend fun getPokemonDetails(id: Int): Pokemon?
    suspend fun getPokemonDetailsByName(name: String): Pokemon?
    suspend fun getPokemonType(pokemonId: Int): String?
    suspend fun clearOldCache()
}