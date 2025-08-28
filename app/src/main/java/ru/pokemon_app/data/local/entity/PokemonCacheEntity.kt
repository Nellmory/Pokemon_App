package ru.pokemon_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "pokemon_cache")
@Serializable
data class PokemonCacheEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val baseExperience: Int,
    val types: String,
    val stats: String,
    val sprites: String,
    val abilities: String,
    val timestamp: Long = System.currentTimeMillis()
)