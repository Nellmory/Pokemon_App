package ru.pokemon_app.domain.usecase

import jakarta.inject.Inject
import ru.pokemon_app.domain.model.Pokemon
import ru.pokemon_app.common.Result
import ru.pokemon_app.domain.repository.PokemonRepository

class GetPokemonDetailsUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(id: Int): Result<Pokemon> {
        return repository.getPokemonDetails(id)
    }
}