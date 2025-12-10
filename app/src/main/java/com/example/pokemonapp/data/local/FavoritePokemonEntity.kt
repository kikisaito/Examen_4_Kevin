package com.example.pokemonapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritePokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val height: Int,
    val weight: Int,
    val types: String // Simple comma separated string for simplicity
)
