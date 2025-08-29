package ru.pokemon_app.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.pokemon_app.common.Result
import ru.pokemon_app.domain.model.PokemonListResponse
import ru.pokemon_app.domain.usecase.GetFilteredPokemonsUseCase
import ru.pokemon_app.domain.usecase.GetPokemonsUseCase
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val getPokemonsUseCase: GetPokemonsUseCase,
    private val getFilteredPokemonsUseCase: GetFilteredPokemonsUseCase
) : ViewModel() {

    private val _pokemonList = MutableStateFlow<PokemonListResponse?>(null)
    val pokemonList: LiveData<PokemonListResponse?> = _pokemonList.asLiveData()

    private val _loading = MutableStateFlow(false)
    val loading: LiveData<Boolean> = _loading.asLiveData()

    private val _loadingMore = MutableStateFlow(false)
    val loadingMore: LiveData<Boolean> = _loadingMore.asLiveData()

    private val _error = MutableStateFlow<String?>(null)
    val error: LiveData<String?> = _error.asLiveData()

    var isFilterOrSearchActive = false
    var canLoadMore = true

    fun loadPokemons(page: Int, query: String? = null) {
        if (page == 1 && query.isNullOrEmpty() && !isFilterOrSearchActive) {
            _loading.value = true
        } else {
            _loadingMore.value = true
        }

        isFilterOrSearchActive = !query.isNullOrEmpty()
        _error.value = null

        viewModelScope.launch {
            try {
                when (val res = getPokemonsUseCase(page, query)) {
                    is Result.Success -> {
                        if (res.data.results.isEmpty()) {
                            // ничего не пришло — дальше грузить бессмысленно
                            canLoadMore = false
                        } else {
                            _pokemonList.value = res.data
                            canLoadMore = true
                        }
                    }
                    is Result.Failure -> {
                        handleError(res.exception, page)
                        // ошибка — значит пока что дальше грузить нельзя
                        canLoadMore = false
                    }
                }
            } catch (e: Exception) {
                handleError(e, page)
            } finally {
                _loading.value = false
                _loadingMore.value = false
            }
        }
    }

    fun loadFilteredPokemons(
        type: String?,
        minHp: Int?,
        minAttack: Int?,
        minDefense: Int?,
        orderBy: String?
    ) {
        isFilterOrSearchActive = true
        _error.value = null
        _loading.value = true

        viewModelScope.launch {
            try {
                when (val result = getFilteredPokemonsUseCase(type, minHp, minAttack, minDefense, orderBy)) {
                    is Result.Success -> _pokemonList.value = result.data
                    is Result.Failure -> _error.value = "Такие покемоны еще не загружены!\n${result.exception.message}"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка фильтрации: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun handleError(exception: Exception, page: Int) {
        if (page == 1) {
            _error.value = when {
                exception.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                    "Нет интернет-соединения"
                exception.message?.contains("timeout", ignoreCase = true) == true ->
                    "Превышено время ожидания"
                else -> "Ошибка: ${exception.message ?: "Неизвестная ошибка"}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}