package com.qtk.kotlintest.api

import com.qtk.kotlintest.App
import com.qtk.kotlintest.extensions.createBody
import com.qtk.kotlintest.retrofit.data.ApiResult
import com.qtk.kotlintest.retrofit.data.ChatBean
import com.qtk.kotlintest.retrofit.service.ChatGPTService
import com.qtk.kotlintest.room.entity.ChatMessageBean
import com.qtk.kotlintest.room.entity.UserMessageBean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatGPTApi @Inject constructor(private val service: ChatGPTService){

    suspend fun sendMessageToChatGPT(messageList: List<Any>): ApiResult<ChatBean> {
        val messages = mutableListOf<Map<String, Any>>()
        for (data in messageList) {
            when(data) {
                is ChatMessageBean -> {
                    messages.add(mapOf(
                        "role" to data.role,
                        "content" to data.content
                    ))
                }
                is UserMessageBean -> {
                    messages.add(mapOf(
                        "role" to data.role,
                        "content" to data.content
                    ))
                }
            }
        }
        val params = mapOf(
            "model" to "gpt-3.5-turbo",
            "messages" to messages
        )
        return service.sendMessageToChatGPT(App.instance.moshi.createBody(params))
    }
}