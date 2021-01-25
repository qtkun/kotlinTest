package com.qtk.kotlintest.paging

import androidx.paging.PagingSource
import com.qtk.kotlintest.api.Api
import com.qtk.kotlintest.retrofit.adapter.ApiException
import com.qtk.kotlintest.retrofit.data.ApiResult
import com.qtk.kotlintest.retrofit.data.GoodsBean
import com.qtk.kotlintest.retrofit.data.ResponseResultList
import okio.IOException
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
            when(val result = api.getGoods(key, params.loadSize, state, orderType, orderField)) {
                is ApiResult.Success -> {
                    val goods = result.data!!.data
                    LoadResult.Page(
                        data = goods,
                        prevKey = null,
                        nextKey = if (goods.size >= params.loadSize) key + 1 else null
                    )
                }
                is ApiResult.Failure -> {
                    LoadResult.Error(ApiException(result.errorCode, result.errorMsg))
                }
            }
        } catch (e : Exception) {
            LoadResult.Error(e)
        }
    }
}