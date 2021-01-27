package com.qtk.kotlintest.api

import com.qtk.kotlintest.extensions.createBody
import com.qtk.kotlintest.retrofit.data.ApiResult
import com.qtk.kotlintest.retrofit.data.GoodsBean
import com.qtk.kotlintest.retrofit.data.ResponseResultList
import com.qtk.kotlintest.retrofit.service.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Api @Inject constructor(private val service: ApiService){

    suspend fun getGoods(page : Int, size : Int, state : Int, orderType :String, orderField : String) : ApiResult<ResponseResultList<GoodsBean>>{
        val map : Map<String, Any> = mapOf(
            "pageindex" to page,
            "pagesize" to size,
            "tagids" to emptyList<String>(),
            "state" to state,
            "ordertype" to orderType,
            "orderfield" to orderField
        )
        return service.getGoods(map.createBody())
    }
}