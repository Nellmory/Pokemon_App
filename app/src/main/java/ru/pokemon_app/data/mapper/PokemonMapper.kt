package ru.pokemon_app.data.mapper

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.pokemon_app.data.local.entity.PokemonCacheEntity
import ru.pokemon_app.data.remote.model.PokemonAbilityDto
import ru.pokemon_app.data.remote.model.PokemonDto
import ru.pokemon_app.data.remote.model.PokemonListItemDto
import ru.pokemon_app.data.remote.model.PokemonSpritesDto
import ru.pokemon_app.data.remote.model.PokemonStatDto
import ru.pokemon_app.data.remote.model.PokemonTypeDto
import ru.pokemon_app.domain.model.Ability
import ru.pokemon_app.domain.model.Pokemon
import ru.pokemon_app.domain.model.PokemonAbility
import ru.pokemon_app.domain.model.PokemonListItem
import ru.pokemon_app.domain.model.PokemonSprites
import ru.pokemon_app.domain.model.PokemonStat
import ru.pokemon_app.domain.model.PokemonType
import ru.pokemon_app.domain.model.Stat
import ru.pokemon_app.domain.model.Type

object PokemonMapper {

    fun mapToDomain(dto: PokemonDto): Pokemon {
        return Pokemon(
            id = dto.id,
            name = dto.name,
            height = dto.height,
            weight = dto.weight,
            baseExperience = dto.baseExperience,
            types = dto.types.map { mapToDomain(it) },
            stats = dto.stats.map { mapToDomain(it) },
            sprites = mapToDomain(dto.sprites),
            abilities = dto.abilities.map { mapToDomain(it) }
        )
    }

    fun mapToDomain(dto: PokemonTypeDto): PokemonType {
        return PokemonType(
            slot = dto.slot,
            type = Type(dto.type.name, dto.type.url)
        )
    }

    fun mapToDomain(dto: PokemonStatDto): PokemonStat {
        return PokemonStat(
            baseStat = dto.baseStat,
            effort = dto.effort,
            stat = Stat(dto.stat.name, dto.stat.url)
        )
    }

    fun mapToDomain(dto: PokemonSpritesDto): PokemonSprites {
        return PokemonSprites(
            frontDefault = dto.frontDefault,
            backDefault = dto.backDefault,
            frontShiny = dto.frontShiny,
            backShiny = dto.backShiny
        )
    }

    fun mapToDomain(dto: PokemonAbilityDto): PokemonAbility {
        return PokemonAbility(
            ability = Ability(dto.ability.name, dto.ability.url),
            isHidden = dto.isHidden,
            slot = dto.slot
        )
    }

    // Entity → Domain
    fun mapToDomain(entity: PokemonCacheEntity): Pokemon {
        val json = Json { ignoreUnknownKeys = true }
        return Pokemon(
            id = entity.id,
            name = entity.name,
            height = entity.height,
            weight = entity.weight,
            baseExperience = entity.baseExperience,
            types = json.decodeFromString(entity.types),
            stats = json.decodeFromString(entity.stats),
            sprites = json.decodeFromString(entity.sprites),
            abilities = json.decodeFromString(entity.abilities)
        )
    }

    // Domain → Entity
    fun mapToEntity(domain: Pokemon): PokemonCacheEntity {
        val json = Json { ignoreUnknownKeys = true }
        val hp = domain.stats.firstOrNull { it.stat.name == "hp" }?.baseStat ?: 0
        val attack = domain.stats.firstOrNull { it.stat.name == "attack" }?.baseStat ?: 0
        val defense = domain.stats.firstOrNull { it.stat.name == "defense" }?.baseStat ?: 0

        return PokemonCacheEntity(
            id = domain.id,
            name = domain.name,
            height = domain.height,
            weight = domain.weight,
            baseExperience = domain.baseExperience,
            types = json.encodeToString(domain.types),
            stats = json.encodeToString(domain.stats),
            sprites = json.encodeToString(domain.sprites),
            abilities = json.encodeToString(domain.abilities),
            hp = hp,
            attack = attack,
            defense = defense,
            timestamp = System.currentTimeMillis()
        )
    }

    // List mappings
    fun mapToDomainList(entities: List<PokemonCacheEntity>): List<Pokemon> {
        return entities.map { mapToDomain(it) }
    }

    fun mapListItemToDomain(dto: PokemonListItemDto): PokemonListItem {
        val id = dto.extractId()
        return PokemonListItem(
            name = dto.name,
            url = dto.url,
            type = null
        )
    }
}