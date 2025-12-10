package com.example.pokemonapp

import android.app.Application
import com.example.pokemonapp.data.local.PokemonDatabase
import com.example.pokemonapp.data.remote.RetrofitInstance
import com.example.pokemonapp.data.repository.PokemonRepository

class PokemonApplication : Application() {
    private val database by lazy { PokemonDatabase.getDatabase(this) }
    val repository by lazy { 
        PokemonRepository(
            RetrofitInstance.api, 
            database.pokemonDao()
        ) 
    }
}
