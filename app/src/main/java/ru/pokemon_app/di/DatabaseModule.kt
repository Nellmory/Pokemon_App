package ru.pokemon_app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.pokemon_app.data.local.dao.PokemonDao
import ru.pokemon_app.data.local.database.AppDatabase
import ru.pokemon_app.data.local.datasource.LocalDataSource

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun providePokemonDao(appDatabase: AppDatabase): PokemonDao {
        return appDatabase.pokemonDao()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(pokemonDao: PokemonDao): LocalDataSource {
        return LocalDataSource(pokemonDao)
    }
}