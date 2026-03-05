package com.example.pokeapp.data.model

import com.google.gson.annotations.SerializedName

// from /pokemon/{pokemonName}

data class Pokemon(
    val name: String,
    val sprites: Sprites,
    val stats: List<StatsInfo>,
)

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String?
)

data class StatsInfo(
    @SerializedName("base_stat")
    val baseStat: Int,
    val stat: Stat
)

data class Stat(
    val name: String
)

// from /type/{typeName}

data class PokemonsByType(
    val pokemon: List<PokemonsByTypeInfo>
)

data class PokemonsByTypeInfo(
    val pokemon: PokemonRef
)

data class PokemonRef(
    val name: String,
    val url: String
)