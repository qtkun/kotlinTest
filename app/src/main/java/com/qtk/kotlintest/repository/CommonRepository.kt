package com.qtk.kotlintest.repository

import androidx.paging.Pager
import androidx.paging.map
import com.qtk.kotlintest.api.Api
import com.qtk.kotlintest.contant.pagingConfig
import com.qtk.kotlintest.paging.PokemonDataSource
import com.qtk.kotlintest.retrofit.data.getId
import com.qtk.kotlintest.retrofit.data.getImageUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonRepository @Inject constructor(
    private val api: Api
) {
    fun getPokemon() = Pager(pagingConfig){
        PokemonDataSource(api)
    }.flow.map {
        it.map { pokemon ->
            pokemon.apply {
                id = getId(pokemon.url)
                url = getImageUrl(pokemon.url)
            }
        }
    }

    fun getPokemon(limit: Int, offset: Int) = flow {
        throw IOException("TEST")
        emit(api.getPokemon(limit, offset))
    }.flowOn(Dispatchers.IO)
}