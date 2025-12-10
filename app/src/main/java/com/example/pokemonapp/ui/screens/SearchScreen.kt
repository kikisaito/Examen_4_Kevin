package com.example.pokemonapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.ui.viewmodel.SearchUiState
import com.example.pokemonapp.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToDetail: (Pokemon) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val favorites by viewModel.favoriteIds.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar Pokemon") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { viewModel.search(query) }) {
                Text("Buscar")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is SearchUiState.Loading -> LoadingScreen()
                is SearchUiState.Error -> Text("Error: ${state.message}")
                is SearchUiState.Idle -> Text("Ingresa un nombre exacto (ej. pikachu)")
                is SearchUiState.Success -> {
                    LazyColumn {
                        items(state.pokemons) { pokemon ->
                             val isFav = favorites.contains(pokemon.id)
                             PokemonItem(
                                pokemon = pokemon,
                                isFavorite = isFav,
                                onItemClick = onNavigateToDetail,
                                onFavoriteToggle = { viewModel.onFavoriteClick(pokemon, isFav) }
                            )
                        }
                    }
                }
            }
        }
    }
}
