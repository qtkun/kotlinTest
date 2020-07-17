package com.qtk.kotlintest.retrofit.data

data class GoodsBean(
    val name: String,
    val code: String,
    val pricemin: Double,
    val pricemax: Double,
    val stock: Double,
    val id: String,
    val state: Int,
    val firstsaletime: String,
    val tags: List<GoodsTagBean>,
    val goodsimage : ImageData
)

data class GoodsTagBean(
    val id: String,
    val name: String,
    val qty: Int
)

data class ImageData(
    val name : String,
    val url : String
)