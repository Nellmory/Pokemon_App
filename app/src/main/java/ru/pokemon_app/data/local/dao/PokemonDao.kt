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

    @Query("SELECT * FROM pokemon_cache WHERE name LIKE '%' || :name || '%'")
    suspend fun searchByName(name: String): List<PokemonCacheEntity>

    @Query("SELECT * FROM pokemon_cache ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPokemons(offset: Int, limit: Int): List<PokemonCacheEntity>

    @Query("SELECT * FROM pokemon_cache WHERE name LIKE '%' || :query || '%' COLLATE NOCASE")
    suspend fun searchPokemons(query: String): List<PokemonCacheEntity>

    @Query("""
        SELECT * FROM pokemon_cache
        WHERE (:type IS NULL OR types LIKE '%' || :type || '%')
        AND (:minHp IS NULL OR hp >= :minHp)
        AND (:minAttack IS NULL OR attack >= :minAttack)
        AND (:minDefense IS NULL OR defense >= :minDefense)
        ORDER BY
            CASE WHEN :orderBy = 'name' THEN name END ASC,
            CASE WHEN :orderBy = 'hp' THEN hp END DESC,
            CASE WHEN :orderBy = 'attack' THEN attack END DESC,
            CASE WHEN :orderBy = 'defense' THEN defense END DESC
    """)
    suspend fun filterPokemons(
        type: String?,
        minHp: Int?,
        minAttack: Int?,
        minDefense: Int?,
        orderBy: String?
    ): List<PokemonCacheEntity>
}