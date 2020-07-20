package com.qtk.kotlintest.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.qtk.kotlintest.paging.CommonRepository
import com.qtk.kotlintest.retrofit.data.GoodsBean

class GoodsViewModel : ViewModel() {

    fun getGoods(state : Int, orderType : String, orderField : String) : LiveData<PagingData<GoodsBean>>{
        return CommonRepository.getGoodsData(state, orderType, orderField).cachedIn(viewModelScope).asLiveData()
    }
}