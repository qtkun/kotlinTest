package com.qtk.kotlintest.view_model

import androidx.databinding.ObservableBoolean
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.qtk.kotlintest.base.BaseViewModel
import com.qtk.kotlintest.paging.CommonRepository
import com.qtk.kotlintest.retrofit.data.PokemonBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(private val commonRepository: CommonRepository) : BaseViewModel() {
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