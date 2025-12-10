package com.example.pokemonapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokemonapp.domain.Pokemon

@Composable
fun PokemonItem(
    pokemon: Pokemon,
    isFavorite: Boolean,
    onItemClick: (Pokemon) -> Unit,
    onFavoriteToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick(pokemon) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = pokemon.name.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium)
                if (pokemon.types.isNotEmpty()) {
                    Text(text = "Types: ${pokemon.types.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = { onFavoriteToggle(isFavorite) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun FavoriteItem(
    pokemon: Pokemon,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = pokemon.name.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
