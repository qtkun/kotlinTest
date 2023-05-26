package com.qtk.kotlintest.api

import com.qtk.kotlintest.App
import com.qtk.kotlintest.extensions.createBody
import com.qtk.kotlintest.retrofit.data.ApiResult
import com.qtk.kotlintest.retrofit.data.ChatBean
import com.qtk.kotlintest.retrofit.service.ChatGPTService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatGPTApi @Inject constructor(private val service: ChatGPTService){

    suspend fun sendMessageToChatGPT(content: String): ApiResult<ChatBean> {
        val params = mapOf(
            "model" to "gpt-3.5-turbo",
            "messages" to listOf(
                mapOf(
                    "role" to "user",
                    "content" to content
                )
            )
        )
        return service.sendMessageToChatGPT(App.instance.moshi.createBody(params))
    }
}