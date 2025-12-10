package com.example.pokemonapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.pokemonapp.ui.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel
) {
    val favorites by viewModel.favorites.collectAsState()

    if (favorites.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tienes favoritos aún.")
        }
    } else {
        LazyColumn(Modifier.fillMaxSize()) {
            items(favorites) { pokemon ->
                FavoriteItem(
                    pokemon = pokemon,
                    onDeleteClick = { viewModel.removeFavorite(pokemon.id) }
                )
            }
        }
    }
}
