package ru.pokemon_app.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.pokemon_app.domain.model.Pokemon
import ru.pokemon_app.domain.model.PokemonListResponse

interface ApiService {

    @GET("pokemon")
    suspend fun getPokemons(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = 20,
        @Query("search") search: String? = null
    ): PokemonListResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonById(@Path("id") id: Int): Pokemon
}