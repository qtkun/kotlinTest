package com.qtk.kotlintest.contant

import androidx.paging.PagingConfig

const val BASE_URL = "https://pokeapi.co/api/v2/"
const val APP_ID = "15646a06818f61f7b8d7823ca833e1ce"
const val URL = "https://api.openweathermap.org/data/2.5/" +
        "forecast/daily?mode=json&units=metric&cnt=7"
const val COMPLETE_URL = "$URL&APPID=$APP_ID&q="


const val DATA_STORE_NAME = "KotlinTest"
const val ZIP_CODE = "zipCode"
const val DEFAULT_ZIP = 94043L
const val BITMAP_ID = "bitmapId"

val pagingConfig = PagingConfig(
    // 每页显示的数据的大小
    pageSize = 20,

    // 开启占位符
    enablePlaceholders = true,

    // 预刷新的距离，距离最后一个 item 多远时加载数据
    // 默认为 pageSize
    prefetchDistance = 4,

    /**
     * 初始化加载数量，默认为 pageSize * 3
     *
     * internal const val DEFAULT_INITIAL_PAGE_MULTIPLIER = 3
     * val initialLoadSize: Int = pageSize * DEFAULT_INITIAL_PAGE_MULTIPLIER
     */

    /**
     * 初始化加载数量，默认为 pageSize * 3
     *
     * internal const val DEFAULT_INITIAL_PAGE_MULTIPLIER = 3
     * val initialLoadSize: Int = pageSize * DEFAULT_INITIAL_PAGE_MULTIPLIER
     */
    initialLoadSize = 20
)