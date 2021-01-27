package com.qtk.kotlintest.view_model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.qtk.kotlintest.paging.CommonRepository
import com.qtk.kotlintest.retrofit.data.PokemonBean

class PokemonViewModel @ViewModelInject constructor(private val commonRepository: CommonRepository) : ViewModel() {
    fun getPokemon(): LiveData<PagingData<PokemonBean>> =
        commonRepository.getPokemon().cachedIn(viewModelScope).asLiveData()
}