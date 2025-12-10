package com.example.pokemonapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.data.repository.PokemonRepository
import com.example.pokemonapp.domain.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val pokemon: Pokemon) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class DetailViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun loadPokemon(id: Int, name: String?, preloaded: Pokemon?) {
        viewModelScope.launch {
            try {
                // Observe favorite state for this ID
                 launch {
                     repository.isFavorite(id).collect {
                         _isFavorite.value = it
                     }
                 }

                if (preloaded != null && preloaded.weight != 0) {
                     // We have full details
                     _uiState.value = DetailUiState.Success(preloaded)
                } else if (name != null) {
                    // Fetch from API
                    val detail = repository.getPokemonDetail(name.lowercase())
                    _uiState.value = DetailUiState.Success(detail)
                } else {
                    _uiState.value = DetailUiState.Error("No data provided")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Error loading detail")
            }
        }
    }

    fun toggleFavorite(currentPokemon: Pokemon) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                repository.removeFromFavorites(currentPokemon.id)
            } else {
                repository.addToFavorites(currentPokemon)
            }
        }
    }
}
