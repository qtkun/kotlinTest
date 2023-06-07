package com.qtk.kotlintest.view_model

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.qtk.kotlintest.base.BaseViewModel
import com.qtk.kotlintest.repository.CommonRepository
import com.qtk.kotlintest.retrofit.data.PokemonBean
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor( val commonRepository: CommonRepository) : BaseViewModel() {
    val refresh = ObservableBoolean()

    fun getPokemon(): LiveData<PagingData<PokemonBean>> =
        commonRepository.getPokemon().cachedIn(viewModelScope).asLiveData()

    fun refreshListener(cls: CombinedLoadStates) {
        when (cls.refresh) {
            is LoadState.Error -> refresh.set(false)
            is LoadState.Loading -> refresh.set(true)
            is LoadState.NotLoading -> refresh.set(false)
        }
    }
}