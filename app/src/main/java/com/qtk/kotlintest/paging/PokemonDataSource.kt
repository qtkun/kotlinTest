package com.qtk.kotlintest.paging

import androidx.paging.PagingSource
import com.qtk.kotlintest.api.Api
import com.qtk.kotlintest.retrofit.adapter.ApiException
import com.qtk.kotlintest.retrofit.data.ApiResult
import com.qtk.kotlintest.retrofit.data.PokemonBean
import java.lang.Exception

class PokemonDataSource(private val api: Api): PagingSource<Int, PokemonBean>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonBean> {
        return try {
            val key = params.key ?: 0
            when(val result = api.getPokemon(params.loadSize, key * params.loadSize)) {
                is ApiResult.Success -> {
                    val pokemon = result.data!!.results
                    LoadResult.Page(
                        data = pokemon,
                        prevKey = null,
                        nextKey = if (pokemon.size >= params.loadSize) key + 1 else null
                    )
                }
                is ApiResult.Failure -> {
                    LoadResult.Error(ApiException(result.errorCode, result.errorMsg, result.url))
                }
            }
        } catch (e : Exception) {
            LoadResult.Error(e)
        }
    }
}