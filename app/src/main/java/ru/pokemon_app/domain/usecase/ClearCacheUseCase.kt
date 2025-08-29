package ru.pokemon_app.domain.usecase

import jakarta.inject.Inject
import ru.pokemon_app.common.Result
import ru.pokemon_app.domain.repository.PokemonRepository

class ClearCacheUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.clearOldCache()
    }
}