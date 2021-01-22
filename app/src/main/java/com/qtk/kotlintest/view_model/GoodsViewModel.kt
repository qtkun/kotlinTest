package com.qtk.kotlintest.view_model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.qtk.kotlintest.paging.CommonRepository
import com.qtk.kotlintest.retrofit.data.GoodsBean

class GoodsViewModel @ViewModelInject constructor(private val commonRepository: CommonRepository) : ViewModel() {

    fun getGoods(state : Int, orderType : String, orderField : String) : LiveData<PagingData<GoodsBean>>{
        return commonRepository.getGoodsData(state, orderType, orderField).cachedIn(viewModelScope).asLiveData()
    }
}