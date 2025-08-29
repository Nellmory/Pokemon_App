package ru.pokemon_app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.pokemon_app.domain.repository.PokemonRepository
import ru.pokemon_app.domain.usecase.ClearCacheUseCase
import ru.pokemon_app.domain.usecase.GetFilteredPokemonsUseCase
import ru.pokemon_app.domain.usecase.GetPokemonDetailsUseCase
import ru.pokemon_app.domain.usecase.GetPokemonsUseCase

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetPokemonsUseCase(repository: PokemonRepository): GetPokemonsUseCase {
        return GetPokemonsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetPokemonDetailsUseCase(repository: PokemonRepository): GetPokemonDetailsUseCase {
        return GetPokemonDetailsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetFilteredPokemonsUseCase(repository: PokemonRepository): GetFilteredPokemonsUseCase {
        return GetFilteredPokemonsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideClearCacheUseCase(repository: PokemonRepository): ClearCacheUseCase {
        return ClearCacheUseCase(repository)
    }
}