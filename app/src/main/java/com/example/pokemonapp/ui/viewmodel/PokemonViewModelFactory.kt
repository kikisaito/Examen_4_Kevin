package com.example.pokemonapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pokemonapp.data.repository.PokemonRepository
import com.example.pokemonapp.util.NetworkConnectivityObserver

class PokemonViewModelFactory(
    private val repository: PokemonRepository,
    private val connectivityObserver: NetworkConnectivityObserver? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LandingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LandingViewModel(repository, connectivityObserver!!) as T
        }
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
