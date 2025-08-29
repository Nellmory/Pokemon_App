package ru.pokemon_app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.pokemon_app.data.local.datasource.LocalDataSource
import ru.pokemon_app.data.remote.datasource.RemoteDataSource
import ru.pokemon_app.data.repository.PokemonRepositoryImpl
import ru.pokemon_app.domain.repository.PokemonRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePokemonRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource
    ): PokemonRepository {
        return PokemonRepositoryImpl(remoteDataSource, localDataSource)
    }
}