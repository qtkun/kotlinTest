package com.qtk.kotlintest.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig

object GoodsRepository {
    fun getGoodsData() = Pager(PagingConfig(pageSize = 10, initialLoadSize = 10)){
        GoodsDataSource()
    }.flow
}