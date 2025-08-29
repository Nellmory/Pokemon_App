package ru.pokemon_app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.pokemon_app.data.remote.api.ApiService
import ru.pokemon_app.data.remote.datasource.RemoteDataSource
import ru.pokemon_app.data.remote.datasource.RetrofitClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitClient.api
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(apiService: ApiService): RemoteDataSource {
        return RemoteDataSource(apiService)
    }
}