package com.example.pokemonapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoritePokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(pokemon: FavoritePokemonEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: Int): Flow<Boolean>

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getFavoriteById(id: Int): FavoritePokemonEntity?
}
