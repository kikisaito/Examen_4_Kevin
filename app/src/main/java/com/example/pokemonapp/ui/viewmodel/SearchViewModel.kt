package com.example.pokemonapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.data.repository.PokemonRepository
import com.example.pokemonapp.domain.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val pokemons: List<Pokemon>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

class SearchViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Map of favorite IDs to sync UI
    val favoriteIds: StateFlow<Set<Int>> = MutableStateFlow(emptySet())
    
    init {
        viewModelScope.launch {
            repository.favoritePokemons.collect { favs ->
                (favoriteIds as MutableStateFlow).value = favs.map { it.id }.toSet()
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                // PokeAPI search by name (returns single item as detail)
                val pokemon = repository.getPokemonDetail(query.trim().lowercase())
                _uiState.value = SearchUiState.Success(listOf(pokemon))
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error("No found or error: ${e.message}")
            }
        }
    }

    fun onFavoriteClick(pokemon: Pokemon, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                repository.removeFromFavorites(pokemon.id)
            } else {
                repository.addToFavorites(pokemon)
            }
        }
    }
}
