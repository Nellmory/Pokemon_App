package ru.pokemon_app.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.pokemon_app.data.local.datasource.LocalDataSource
import ru.pokemon_app.data.mapper.PokemonMapper
import ru.pokemon_app.data.remote.datasource.RemoteDataSource
import ru.pokemon_app.common.Result
import ru.pokemon_app.domain.model.Pokemon
import ru.pokemon_app.domain.model.PokemonListResponse
import ru.pokemon_app.domain.model.PokemonListItem
import ru.pokemon_app.domain.repository.PokemonRepository
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

class PokemonRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PokemonRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val cacheTimeout = 24 * 60 * 60 * 1000L

    override suspend fun getPokemons(page: Int, query: String?): Result<PokemonListResponse> {
        return withContext(dispatcher) {
            try {
                if (query.isNullOrEmpty()) {
                    val offset = (page - 1) * 20
                    when (val result = remoteDataSource.getPokemons(offset, 20)) {
                        is Result.Success -> {
                            val baseResponse = PokemonListResponse(
                                count = result.data.count,
                                next = result.data.next,
                                previous = result.data.previous,
                                results = emptyList()
                            )

                            val enrichedResults = mutableListOf<PokemonListItem>()
                            var hasNetworkError = false

                            for (itemDto in result.data.results) {
                                try {
                                    val pokemonResult = getPokemonDetails(itemDto.extractId())
                                    val typeName = when (pokemonResult) {
                                        is Result.Success -> pokemonResult.data.types.firstOrNull()?.type?.name
                                        is Result.Failure -> {
                                            hasNetworkError = true
                                            null
                                        }
                                    }
                                    enrichedResults.add(
                                        PokemonMapper.mapListItemToDomain(itemDto).copy(type = typeName)
                                    )
                                } catch (e: Exception) {
                                    hasNetworkError = true
                                    enrichedResults.add(PokemonMapper.mapListItemToDomain(itemDto))
                                }
                            }

                            if (hasNetworkError && enrichedResults.isEmpty()) {
                                Result.Failure(Exception("Network error occurred"))
                            } else {
                                Result.Success(baseResponse.copy(results = enrichedResults))
                            }
                        }
                        is Result.Failure -> {
                            getPokemonsFromCache(page, null)
                        }
                    }
                } else {
                    getPokemonsFromCache(page, query)
                }
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
    }

    private suspend fun getPokemonsFromCache(page: Int, query: String?): Result<PokemonListResponse> {
        return try {
            val offset = (page - 1) * 20
            val cachedResult = if (query.isNullOrEmpty()) {
                localDataSource.getPokemons(offset, 20)
            } else {
                localDataSource.searchPokemons(query)
            }

            when (cachedResult) {
                is Result.Success -> {
                    val pokemons = PokemonMapper.mapToDomainList(cachedResult.data)
                    Result.Success(
                        PokemonListResponse(
                            count = pokemons.size,
                            next = null,
                            previous = null,
                            results = pokemons.map {
                                val typeName = it.types.firstOrNull()?.type?.name
                                PokemonListItem(
                                    name = it.name,
                                    url = "offline/${it.id}",
                                    type = typeName
                                )
                            }
                        )
                    )
                }
                is Result.Failure -> Result.Failure(cachedResult.exception)
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getPokemonDetails(id: Int): Result<Pokemon> {
        return withContext(dispatcher) {
            try {
                when (val result = remoteDataSource.getPokemonById(id)) {
                    is Result.Success -> {
                        val pokemon = PokemonMapper.mapToDomain(result.data)
                        savePokemonToCache(pokemon)
                        Result.Success(pokemon)
                    }
                    is Result.Failure -> {
                        when (val cachedResult = localDataSource.getPokemonById(id)) {
                            is Result.Success -> {
                                cachedResult.data?.let { entity ->
                                    Result.Success(PokemonMapper.mapToDomain(entity))
                                } ?: Result.Failure(Exception("Pokemon not found in cache"))
                            }
                            is Result.Failure -> Result.Failure(cachedResult.exception)
                        }
                    }
                }
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
    }

    override suspend fun getFilteredPokemons(
        type: String?,
        minHp: Int?,
        minAttack: Int?,
        minDefense: Int?,
        orderBy: String?
    ): Result<PokemonListResponse> {
        return withContext(dispatcher) {
            try {
                val filterResult = localDataSource.filterPokemons(type, minHp, minAttack, minDefense, orderBy)
                when (filterResult) {
                    is Result.Success -> {
                        val pokemons = PokemonMapper.mapToDomainList(filterResult.data)
                        if (pokemons.isNotEmpty()) {
                            Result.Success(
                                PokemonListResponse(
                                    count = pokemons.size,
                                    next = null,
                                    previous = null,
                                    results = pokemons.map {
                                        val typeName = it.types.firstOrNull()?.type?.name
                                        PokemonListItem(
                                            name = it.name,
                                            url = "offline/${it.id}",
                                            type = typeName
                                        )
                                    }
                                )
                            )
                        } else {
                            Result.Failure(Exception("No pokemons found with these filters"))
                        }
                    }
                    is Result.Failure -> Result.Failure(filterResult.exception)
                }
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
    }

    override suspend fun clearOldCache(): Result<Unit> {
        return withContext(dispatcher) {
            try {
                val cutoffTime = System.currentTimeMillis() - cacheTimeout
                when (val result = localDataSource.clearOldCache(cutoffTime)) {
                    is Result.Success -> Result.Success(Unit)
                    is Result.Failure -> Result.Failure(result.exception)
                }
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
    }

    private suspend fun savePokemonToCache(pokemon: Pokemon) {
        try {
            val entity = PokemonMapper.mapToEntity(pokemon)
            localDataSource.insertPokemon(entity)
        } catch (e: Exception) {
        }
    }

    private fun extractIdFromUrl(url: String): Int {
        return url.trimEnd('/').split('/').last().toInt()
    }
}