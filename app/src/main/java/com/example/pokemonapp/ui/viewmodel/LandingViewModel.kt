package com.example.pokemonapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.data.repository.PokemonRepository
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.util.NetworkConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LandingUiState {
    object Loading : LandingUiState()
    data class Success(val pokemons: List<Pokemon>) : LandingUiState()
    data class Error(val message: String) : LandingUiState()
    object Offline : LandingUiState()
}

class LandingViewModel(
    private val repository: PokemonRepository,
    private val connectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow<LandingUiState>(LandingUiState.Loading)
    val uiState: StateFlow<LandingUiState> = _uiState.asStateFlow()

    private var currentOffset = 0
    private val limit = 20
    private var isLastPage = false
    private val _pokemonList = mutableListOf<Pokemon>()
    
    // We observe favorites to syncing UI
    private val favoritesFlow = repository.favoritePokemons

    init {
        checkConnectionAndLoad()
    }

    private fun checkConnectionAndLoad() {
        viewModelScope.launch {
            if (connectivityObserver.isOnline()) {
                loadPokemons()
            } else {
                _uiState.value = LandingUiState.Offline
            }
        }
    }

    fun loadPokemons() {
        if (isLastPage) return
        viewModelScope.launch {
            try {
                if (_pokemonList.isEmpty()) _uiState.value = LandingUiState.Loading
                
                val newPokemons = repository.getPokemonList(limit, currentOffset)
                if (newPokemons.size < limit) isLastPage = true
                
                _pokemonList.addAll(newPokemons)
                currentOffset += limit
                
                _uiState.value = LandingUiState.Success(_pokemonList.toList())
            } catch (e: Exception) {
                _uiState.value = LandingUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleFavorite(pokemon: Pokemon) {
        viewModelScope.launch {
            // Check if it is currently favorite (using a simple check or the flow)
            // Ideally we check DB state, but for list item toggle we can assume logic:
            // This requires we know if it IS favorite. 
            // For the landing list, we might want to map favorites.
            // For now, let's just add relying on the DB merge or simple logic.
            // But we need to know whether to add or remove.
            // Simplified: The UI should pass if it thinks it is favorite.
            // Better: Let's read the flow.
             
            // Since this function is called from UI which "knows" the state, let's trust UI or check DB.
            // But 'toggle' implies we switch.
            // Let's implement a check.
        }
    }
    
    // Better approach: Expose favorites ID set so UI can decide if it's favorite
    val favoriteIds: StateFlow<Set<Int>> = MutableStateFlow(emptySet())
    
    init {
        viewModelScope.launch {
            favoritesFlow.collect { favs ->
                (favoriteIds as MutableStateFlow).value = favs.map { it.id }.toSet()
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
