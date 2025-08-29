package ru.pokemon_app.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.pokemon_app.domain.model.PokemonListResponse
import ru.pokemon_app.data.repository.PokemonRepository
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonList = MutableStateFlow<PokemonListResponse?>(null)
    val pokemonList: LiveData<PokemonListResponse?> = _pokemonList.asLiveData()

    private val _loading = MutableStateFlow(false)
    val loading: LiveData<Boolean> = _loading.asLiveData()

    private val _error = MutableStateFlow<String?>(null)
    val error: LiveData<String?> = _error.asLiveData()

    var isFilterOrSearchActive = false

    fun loadPokemons(page: Int, query: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val result = repository.getPokemons(page, query)
                if (result != null) {
                    _pokemonList.value = result
                } else {
                    _error.value = "Не удалось загрузить покемонов"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _loading.value = false
            }

            isFilterOrSearchActive = !query.isNullOrEmpty()
        }
    }

    fun loadFilteredPokemons(
        type: String?,
        minHp: Int?,
        minAttack: Int?,
        minDefense: Int?,
        orderBy: String?
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getFilteredPokemons(type, minHp, minAttack, minDefense, orderBy)
                if (result != null && result.results.isNotEmpty()) {
                    _pokemonList.value = result
                } else {
                    _error.value = """
                        Такие покемоны еще не загружены!
                        """.trimIndent()
                }
            } finally {
                _loading.value = false
            }

            isFilterOrSearchActive = true
        }
    }
}