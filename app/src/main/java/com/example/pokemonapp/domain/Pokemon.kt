package com.example.pokemonapp.domain

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val height: Int = 0,
    val weight: Int = 0,
    val types: List<String> = emptyList(),
    val isFavorite: Boolean = false
)
