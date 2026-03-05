package com.example.pokeapp.data.repository

import com.example.pokeapp.data.model.Pokemon
import com.example.pokeapp.data.model.PokemonRef
import com.example.pokeapp.data.network.RetrofitClient

class PokemonRepository {

    private val apiService = RetrofitClient.apiService

    // in-memory caches
    private val typeCache = mutableMapOf<String, List<PokemonRef>>()
    private val pokemonCache = mutableMapOf<String, Pokemon>()

    // both return cached if available, otherwise fetch from the API
    suspend fun getPokemonsByType(typeName: String): List<PokemonRef> {
        typeCache[typeName]?.let { return it }
        val response = apiService.getPokemonsByType(typeName)
        val list = response.pokemon.map { it.pokemon }
        typeCache[typeName] = list
        return list
    }

    suspend fun getPokemon(name: String): Pokemon {
        pokemonCache[name]?.let { return it }
        val pokemon = apiService.getPokemon(name)
        pokemonCache[name] = pokemon
        return pokemon
    }
}