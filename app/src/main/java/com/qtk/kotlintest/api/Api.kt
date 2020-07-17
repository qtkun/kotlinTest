package com.qtk.kotlintest.api

import com.qtk.kotlintest.retrofit.data.GoodsBean
import com.qtk.kotlintest.retrofit.manager.Manager

object Api {
    suspend fun getGoods(page : Int, size : Int) : List<GoodsBean>{
        val map : Map<String, Any> = mapOf(
            "pageindex" to page,
            "pagesize" to size,
            "tagids" to emptyList<String>(),
            "state" to 0,
            "ordertype" to "1",
            "orderfield" to "createtime"
        )
        return Manager.service.getGoods(Manager.createBody(map)).data
    }
}