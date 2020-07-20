package com.qtk.kotlintest.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig

object CommonRepository {
    fun getGoodsData(
        state : Int,
        orderType : String,
        orderField : String
    ) = Pager(PagingConfig(pageSize = 10, initialLoadSize = 10)){
        GoodsDataSource(state, orderType, orderField)
    }.flow
}