package com.qtk.kotlintest.api

import com.qtk.kotlintest.retrofit.data.*
import com.qtk.kotlintest.retrofit.service.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Api @Inject constructor(private val service: ApiService){
    suspend fun getPokemon(limit: Int, offset: Int): ApiResult<PokemonListBean>{
        return service.getPokemonList(limit, offset)
    }
}