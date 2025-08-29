package ru.pokemon_app.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import ru.pokemon_app.common.Result
import ru.pokemon_app.domain.model.Pokemon
import ru.pokemon_app.domain.usecase.GetPokemonDetailsUseCase

@HiltViewModel
class PokemonDetailsViewModel @Inject constructor(
    private val getPokemonDetailsUseCase: GetPokemonDetailsUseCase
) : ViewModel() {

    private val _pokemon = MutableLiveData<Pokemon>()
    val pokemon: LiveData<Pokemon> = _pokemon

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadPokemonDetails(id: Int) {
        viewModelScope.launch {
            when (val result = getPokemonDetailsUseCase(id)) {
                is Result.Success -> _pokemon.value = result.data
                is Result.Failure -> _error.value = "Не удалось загрузить детали"
            }
        }
    }
}