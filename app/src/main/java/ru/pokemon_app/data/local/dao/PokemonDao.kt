package ru.pokemon_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.pokemon_app.data.local.entity.PokemonCacheEntity

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonCacheEntity)

    @Query("SELECT * FROM pokemon_cache WHERE id = :id")
    suspend fun getPokemonById(id: Int): PokemonCacheEntity?

    @Query("SELECT * FROM pokemon_cache WHERE name = :name")
    suspend fun getPokemonByName(name: String): PokemonCacheEntity?

    @Query("DELETE FROM pokemon_cache WHERE timestamp < :timestamp")
    suspend fun clearOldCache(timestamp: Long)

    @Query("DELETE FROM pokemon_cache")
    suspend fun clearAllCache()
}