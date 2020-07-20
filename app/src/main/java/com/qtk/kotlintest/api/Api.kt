package com.qtk.kotlintest.api

import com.qtk.kotlintest.retrofit.data.GoodsBean
import com.qtk.kotlintest.retrofit.manager.Manager

object Api {
    suspend fun getGoods(page : Int, size : Int, state : Int, orderType :String, orderField : String) : List<GoodsBean>{
        val map : Map<String, Any> = mapOf(
            "pageindex" to page,
            "pagesize" to size,
            "tagids" to emptyList<String>(),
            "state" to state,
            "ordertype" to orderType,
            "orderfield" to orderField
        )
        return Manager.service.getGoods(Manager.createBody(map)).data
    }
}