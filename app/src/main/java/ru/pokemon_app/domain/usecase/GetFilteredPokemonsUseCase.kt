package ru.pokemon_app.domain.usecase

import jakarta.inject.Inject
import ru.pokemon_app.domain.model.PokemonListResponse
import ru.pokemon_app.domain.repository.PokemonRepository
import ru.pokemon_app.common.Result

class GetFilteredPokemonsUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(
        type: String?,
        minHp: Int?,
        minAttack: Int?,
        minDefense: Int?,
        orderBy: String?
    ): Result<PokemonListResponse> {
        return repository.getFilteredPokemons(type, minHp, minAttack, minDefense, orderBy)
    }
}