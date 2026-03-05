package com.example.pokeapp.data.network

import com.example.pokeapp.data.model.Pokemon
import com.example.pokeapp.data.model.PokemonsByType
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonApiService {

    @GET("type/{whateverType}")
    suspend fun getPokemonsByType(
        @Path("whateverType") typeName: String
    ): PokemonsByType

    @GET("pokemon/{whateverName}")
    suspend fun getPokemon(
        @Path("whateverName") name: String
    ): Pokemon
}

