package com.qtk.kotlintest.retrofit.service

import com.qtk.kotlintest.retrofit.data.ApiResult
import com.qtk.kotlintest.retrofit.data.ChatBean
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatGPTService {
    @POST("v1/chat/completions")
    suspend fun sendMessageToChatGPT(@Body body: RequestBody): ApiResult<ChatBean>
}