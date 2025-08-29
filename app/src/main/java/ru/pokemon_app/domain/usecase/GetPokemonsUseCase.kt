package ru.pokemon_app.domain.usecase

import jakarta.inject.Inject
import ru.pokemon_app.common.Result
import ru.pokemon_app.domain.model.PokemonListResponse
import ru.pokemon_app.domain.repository.PokemonRepository

class GetPokemonsUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(page: Int, query: String?): Result<PokemonListResponse> {
        return repository.getPokemons(page, query)
    }
}