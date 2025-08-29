package ru.pokemon_app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.pokemon_app.data.repository.PokemonRepositoryImpl
import ru.pokemon_app.domain.repository.PokemonRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePokemonRepository(@ApplicationContext context: Context): PokemonRepository {
        return PokemonRepositoryImpl(context)
    }
}