package com.qtk.kotlintest.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.qtk.kotlintest.paging.GoodsRepository
import com.qtk.kotlintest.retrofit.data.GoodsBean
import kotlinx.coroutines.launch

class GoodsViewModel : ViewModel() {

    fun getGoods() : LiveData<PagingData<GoodsBean>>{
        return GoodsRepository.getGoodsData().cachedIn(viewModelScope).asLiveData()
    }
}