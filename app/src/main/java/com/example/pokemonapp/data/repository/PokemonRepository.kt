package com.example.pokemonapp.data.repository

import com.example.pokemonapp.data.local.FavoritePokemonEntity
import com.example.pokemonapp.data.local.PokemonDao
import com.example.pokemonapp.data.remote.PokeApi
import com.example.pokemonapp.data.remote.PokemonDetailDto
import com.example.pokemonapp.domain.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PokemonRepository(
    private val api: PokeApi,
    private val dao: PokemonDao
) {
    // Network
    suspend fun getPokemonList(limit: Int, offset: Int): List<Pokemon> {
        val response = api.getPokemonList(limit, offset)
        return response.results.map { dto ->
            // Extract ID from URL for simplicity: "https://pokeapi.co/api/v2/pokemon/1/"
            val id = dto.url.trimEnd('/').substringAfterLast('/').toInt()
            Pokemon(
                id = id,
                name = dto.name,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
            )
        }
    }

    suspend fun getPokemonDetail(nameOrId: String): Pokemon {
        return try {
            val dto = api.getPokemonDetail(nameOrId)
            mapDtoToDomain(dto)
        } catch (e: Exception) {
            // Fallback: Check if it's a number (ID) and try local
            val id = nameOrId.toIntOrNull()
            if (id != null) {
                val local = dao.getFavoriteById(id)
                if (local != null) {
                    return Pokemon(
                        id = local.id,
                        name = local.name,
                        imageUrl = local.imageUrl,
                        height = local.height,
                        weight = local.weight,
                        types = local.types.split(",").filter { it.isNotEmpty() },
                        isFavorite = true
                    )
                }
            }
            throw e
        }
    }

    // Local (Favorites)
    val favoritePokemons: Flow<List<Pokemon>> = dao.getAllFavorites().map { entities ->
        entities.map { entity ->
            Pokemon(
                id = entity.id,
                name = entity.name,
                imageUrl = entity.imageUrl,
                height = entity.height,
                weight = entity.weight,
                types = entity.types.split(",").filter { it.isNotEmpty() },
                isFavorite = true
            )
        }
    }

    suspend fun addToFavorites(pokemon: Pokemon) {
        val entity = FavoritePokemonEntity(
            id = pokemon.id,
            name = pokemon.name,
            imageUrl = pokemon.imageUrl,
            height = pokemon.height,
            weight = pokemon.weight,
            types = pokemon.types.joinToString(",")
        )
        dao.insertFavorite(entity)
    }

    suspend fun removeFromFavorites(id: Int) {
        dao.deleteFavoriteById(id)
    }

    fun isFavorite(id: Int): Flow<Boolean> = dao.isFavorite(id)

    // Helper
    private fun mapDtoToDomain(dto: PokemonDetailDto): Pokemon {
        return Pokemon(
            id = dto.id,
            name = dto.name,
            imageUrl = dto.sprites.other?.officialArtwork?.frontDefault ?: dto.sprites.frontDefault ?: "",
            height = dto.height,
            weight = dto.weight,
            types = dto.types.map { it.type.name }
        )
    }
}
