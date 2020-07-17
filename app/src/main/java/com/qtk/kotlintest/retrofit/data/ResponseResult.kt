package com.qtk.kotlintest.retrofit.data

data class ResponseResult<T>(
    val code: Int,
    val msg: String,
    val data: T)

data class ResponseResultList<T>(
    val code: Int,
    val msg: String,
    val data: List<T>)