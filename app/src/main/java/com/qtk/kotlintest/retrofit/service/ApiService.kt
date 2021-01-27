package com.qtk.kotlintest.retrofit.service

import com.qtk.kotlintest.retrofit.data.*
import retrofit2.http.*

interface ApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0): ApiResult<PokemonListBean>
}