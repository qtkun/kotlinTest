package com.qtk.kotlintest.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.qtk.kotlintest.api.Api
import com.qtk.kotlintest.contant.pagingConfig
import com.qtk.kotlintest.retrofit.data.getId
import com.qtk.kotlintest.retrofit.data.getImageUrl
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommonRepository @Inject constructor(private val api: Api) {
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
}