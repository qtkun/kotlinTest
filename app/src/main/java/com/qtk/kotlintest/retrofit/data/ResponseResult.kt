package com.qtk.kotlintest.retrofit.data

data class ResponseResult<T>(
    val code: Int,
    val msg: String,
    val data: T
)

data class ResponseResultList<T>(
    val code: Int,
    val msg: String,
    val data: List<T>
)

/**
 * 客户端本地定义的网络请求的 errorCode 和 errorMsg
 *
 * 这里的errorCode < 0，为了与服务器返回的errorCode做区分，服务器返回的 errorCode > 0
 */
object ApiError {
    //数据是null
    val dataIsNull = Error(-1,"data is null")
    //http status code 不是 成功
    val httpStatusCodeError = Error(-2,"Server error. Please try again later.")
    //未知异常
    val unknownException = Error(-3,"unknown exception")
}

data class Error(val errorCode:Int,val errorMsg:String)

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T?) : ApiResult<T>()
    data class Failure(val errorCode: Int, val errorMsg: String, val url: String) : ApiResult<Nothing>()
}