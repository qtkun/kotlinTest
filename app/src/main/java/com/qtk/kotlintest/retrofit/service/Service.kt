package com.qtk.kotlintest.retrofit.service

import com.qtk.kotlintest.retrofit.data.ApiResult
import com.qtk.kotlintest.retrofit.data.GoodsBean
import com.qtk.kotlintest.retrofit.data.ResponseResult
import com.qtk.kotlintest.retrofit.data.ResponseResultList
import okhttp3.RequestBody
import retrofit2.http.*

interface Service {
    @POST("/goods/list")
    suspend fun getGoods(@Body body: RequestBody) : ApiResult<ResponseResultList<GoodsBean>>
}