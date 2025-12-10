package com.example.pokemonapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.data.repository.PokemonRepository
import com.example.pokemonapp.domain.Pokemon
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    val favorites: StateFlow<List<Pokemon>> = repository.favoritePokemons
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun removeFavorite(id: Int) {
        viewModelScope.launch {
            repository.removeFromFavorites(id)
        }
    }
}
