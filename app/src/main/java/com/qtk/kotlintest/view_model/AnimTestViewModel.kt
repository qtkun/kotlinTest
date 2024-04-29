package com.qtk.kotlintest.view_model

import androidx.lifecycle.viewModelScope
import com.qtk.kotlintest.base.base.BaseViewModel
import com.qtk.kotlintest.repository.CommonRepository
import com.qtk.kotlintest.retrofit.data.ApiResult
import com.qtk.kotlintest.retrofit.data.PokemonBean
import com.qtk.kotlintest.retrofit.data.getId
import com.qtk.kotlintest.retrofit.data.getImageUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimTestViewModel @Inject constructor(private val commonRepository: CommonRepository) :
    BaseViewModel() {

    private val _list = MutableStateFlow<MutableList<PokemonBean>>(mutableListOf())
    val list: StateFlow<MutableList<PokemonBean>> get() = _list

    fun postList(list: MutableList<PokemonBean>) {
        _list.value = list
    }

    fun getPokemon(limit: Int, offset: Int) = viewModelScope.launch(CoroutineExceptionHandler {msg, e ->
        e.printStackTrace()
    }) {
        commonRepository
            .getPokemon(limit, offset)
            .baseLoading()
            .collectLatest {
                if (it is ApiResult.Success) {
                    it.data?.results?.let {
                        _list.value = mutableListOf<PokemonBean>().apply {
                            addAll(it.map { pokemon ->
                                pokemon.apply {
                                    id = getId(url)
                                    url = getImageUrl(url)
                                }
                            })
                        }
                    }
                }
            }
    }

}