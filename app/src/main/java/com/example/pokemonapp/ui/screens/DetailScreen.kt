package com.example.pokemonapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokemonapp.ui.viewmodel.DetailUiState
import com.example.pokemonapp.ui.viewmodel.DetailViewModel

@Composable
fun DetailScreen(
    viewModel: DetailViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (uiState is DetailUiState.Success) {
                FloatingActionButton(onClick = { 
                    viewModel.toggleFavorite((uiState as DetailUiState.Success).pokemon) 
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle Favorite"
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is DetailUiState.Loading -> LoadingScreen()
                is DetailUiState.Error -> Text("Error: ${state.message}", modifier = Modifier.align(Alignment.Center))
                is DetailUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = state.pokemon.imageUrl,
                            contentDescription = state.pokemon.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Fit
                        )
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = state.pokemon.name.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("ID: #${state.pokemon.id}")
                            Text("Height: ${state.pokemon.height}")
                            Text("Weight: ${state.pokemon.weight}")
                            
                            val types = state.pokemon.types
                            if (types.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Types: ${types.joinToString(", ")}")
                            }
                        }
                    }
                }
            }
        }
    }
}
