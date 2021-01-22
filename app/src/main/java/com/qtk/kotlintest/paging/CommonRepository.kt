package com.qtk.kotlintest.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.qtk.kotlintest.api.Api
import javax.inject.Inject

class CommonRepository @Inject constructor(private val api: Api) {
    fun getGoodsData(
        state : Int,
        orderType : String,
        orderField : String
    ) = Pager(PagingConfig(pageSize = 10, initialLoadSize = 10)){
        GoodsDataSource(state, orderType, orderField, api)
    }.flow
}