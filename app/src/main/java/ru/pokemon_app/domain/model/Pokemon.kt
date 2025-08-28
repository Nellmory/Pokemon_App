package ru.pokemon_app.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItem>
)

@Serializable
data class PokemonListItem(
    val name: String,
    val url: String,
    val type: String? = null
)

@Serializable
data class Pokemon(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    @SerialName("base_experience") val baseExperience: Int,
    val types: List<PokemonType>,
    val stats: List<PokemonStat>,
    val sprites: PokemonSprites,
    val abilities: List<PokemonAbility>
)

@Serializable
data class PokemonType(
    val slot: Int,
    val type: Type
)

@Serializable
data class Type(
    val name: String,
    val url: String
)

@Serializable
data class PokemonStat(
    @SerialName("base_stat") val baseStat: Int,
    val effort: Int,
    val stat: Stat
)

@Serializable
data class Stat(
    val name: String,
    val url: String
)

@Serializable
data class PokemonSprites(
    @SerialName("front_default") val frontDefault: String?,
    @SerialName("back_default") val backDefault: String?,
    @SerialName("front_shiny") val frontShiny: String?,
    @SerialName("back_shiny") val backShiny: String?
)

@Serializable
data class PokemonAbility(
    val ability: Ability,
    @SerialName("is_hidden") val isHidden: Boolean,
    val slot: Int
)

@Serializable
data class Ability(
    val name: String,
    val url: String
)