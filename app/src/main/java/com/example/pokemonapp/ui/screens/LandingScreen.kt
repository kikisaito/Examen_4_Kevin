package com.example.pokemonapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.ui.viewmodel.LandingUiState
import com.example.pokemonapp.ui.viewmodel.LandingViewModel

@Composable
fun LandingScreen(
    viewModel: LandingViewModel,
    onNavigateToDetail: (Pokemon) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val favorites by viewModel.favoriteIds.collectAsState()
    val listState = rememberLazyListState()

    // Scroll listener for pagination
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && 
                    uiState is LandingUiState.Success && 
                    lastIndex >= (uiState as LandingUiState.Success).pokemons.size - 2) {
                    viewModel.loadPokemons()
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is LandingUiState.Loading -> LoadingScreen()
            is LandingUiState.Offline -> OfflineScreen(onNavigateToFavorites)
            is LandingUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Error: ${state.message}") }
            is LandingUiState.Success -> {
                LazyColumn(state = listState) {
                    items(state.pokemons) { pokemon ->
                        val isFav = favorites.contains(pokemon.id)
                        PokemonItem(
                            pokemon = pokemon,
                            isFavorite = isFav,
                            onItemClick = onNavigateToDetail,
                            onFavoriteToggle = { viewModel.onFavoriteClick(pokemon, isFav) }
                        )
                    }
                    item {
                        // Footer loader
                        LoadingScreen()
                    }
                }
            }
        }
        
        // Buttons
        Column(
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
             FloatingActionButton(
                onClick = onNavigateToSearch,
                modifier = Modifier
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
             Button(onClick = onNavigateToFavorites) {
                Text("Favoritos")
            }
        }
    }
}
