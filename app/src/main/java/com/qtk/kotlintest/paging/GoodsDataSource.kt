package com.qtk.kotlintest.paging

import androidx.paging.PagingSource
import com.qtk.kotlintest.api.Api
import com.qtk.kotlintest.retrofit.data.GoodsBean
import java.lang.Exception
import javax.inject.Inject

class GoodsDataSource constructor(
    private val state : Int,
    private val orderType : String,
    private val orderField : String,
    private val api: Api
) : PagingSource<Int, GoodsBean>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GoodsBean> {
        return try {
            val key = params.key ?: 1
            val goods = api.getGoods(key, params.loadSize, state, orderType, orderField)
            LoadResult.Page(
                data = goods,
                prevKey = null,
                nextKey = if (goods.size >= params.loadSize) key + 1 else null
            )
        } catch (e : Exception) {
            LoadResult.Error(e)
        }
    }
}