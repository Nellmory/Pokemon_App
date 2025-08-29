package ru.pokemon_app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItemDto>
)

@Serializable
data class PokemonListItemDto(
    val name: String,
    val url: String
) {
    fun extractId(): Int {
        return url.split("/").filter { it.isNotBlank() }.last().toInt()
    }
}

@Serializable
data class PokemonDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    @SerialName("base_experience") val baseExperience: Int,
    val types: List<PokemonTypeDto>,
    val stats: List<PokemonStatDto>,
    val sprites: PokemonSpritesDto,
    val abilities: List<PokemonAbilityDto>
)

@Serializable
data class PokemonTypeDto(
    val slot: Int,
    val type: TypeDto
)

@Serializable
data class TypeDto(
    val name: String,
    val url: String
)

@Serializable
data class PokemonStatDto(
    @SerialName("base_stat") val baseStat: Int,
    val effort: Int,
    val stat: StatDto
)

@Serializable
data class StatDto(
    val name: String,
    val url: String
)

@Serializable
data class PokemonSpritesDto(
    @SerialName("front_default") val frontDefault: String?,
    @SerialName("back_default") val backDefault: String?,
    @SerialName("front_shiny") val frontShiny: String?,
    @SerialName("back_shiny") val backShiny: String?
)

@Serializable
data class PokemonAbilityDto(
    val ability: AbilityDto,
    @SerialName("is_hidden") val isHidden: Boolean,
    val slot: Int
)

@Serializable
data class AbilityDto(
    val name: String,
    val url: String
)