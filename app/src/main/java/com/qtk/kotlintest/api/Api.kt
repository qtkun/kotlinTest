package com.qtk.kotlintest.api

import com.qtk.kotlintest.App
import com.qtk.kotlintest.extensions.createBody
import com.qtk.kotlintest.retrofit.data.*
import com.qtk.kotlintest.retrofit.service.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Api @Inject constructor(private val service: ApiService){
    suspend fun getPokemon(limit: Int, offset: Int): ApiResult<PokemonListBean>{
        return service.getPokemonList(limit, offset)
    }
}