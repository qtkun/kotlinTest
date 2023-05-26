package com.qtk.kotlintest.contant

import android.Manifest
import androidx.paging.PagingConfig

const val BASE_URL = "https://api.openai.com/"
const val POKEMON_BASE_URL = "https://pokeapi.co/api/v2/"
const val APP_ID = "15646a06818f61f7b8d7823ca833e1ce"
const val URL = "https://api.openweathermap.org/data/2.5/" +
        "forecast/daily?mode=json&units=metric&cnt=7"
const val COMPLETE_URL = "$URL&APPID=$APP_ID&q="

const val RONG_IM_URL = "https://api-cn.ronghub.com/"

const val OPEN_AI_API_KEY = "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"


const val DATA_STORE_NAME = "KotlinTest"
const val ZIP_CODE = "zipCode"
const val DEFAULT_ZIP = 94043L
const val BITMAP_ID = "bitmapId"
const val IM_TOKEN = "im_token"
const val IM_USER_ID = "im_user_id"
const val IM_APP_KEY = "bmdehs6pbf3os"
const val IM_APP_SECRET = "ciPCmLbj3II"
const val REMOVE_CONVERSATION = "remove_conversation"

val locationPermission = arrayOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION)

val bluetoothPermission = arrayOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_SCAN
)

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