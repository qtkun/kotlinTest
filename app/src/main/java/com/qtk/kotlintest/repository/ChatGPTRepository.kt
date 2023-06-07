package com.qtk.kotlintest.repository

import com.qtk.kotlintest.api.ChatGPTApi
import com.qtk.kotlintest.room.ChatGPTDao
import com.qtk.kotlintest.room.entity.ChatMessageBean
import com.qtk.kotlintest.room.entity.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatGPTRepository@Inject constructor(
    private val gptApi: ChatGPTApi,
    private val chatGPTDao: ChatGPTDao
) {
    fun sendMessageToChatGPT(messageList: List<Any>) = flow {
        emit(gptApi.sendMessageToChatGPT(messageList))
    }.flowOn(Dispatchers.IO)

    fun getMessageFromRoom() = flow {
        emit(chatGPTDao.getChatMessageList())
    }.flowOn(Dispatchers.IO)
        .map {
            it?.map { message ->
                if (message.role == Role.USER) message.mapToUserMessage() else message
            } ?: emptyList()
        }

    suspend fun insertMessage(message: ChatMessageBean) {
        chatGPTDao.insertChatMessage(message)
    }

    suspend fun deleteAllMessages() {
        chatGPTDao.deleteAllMessages()
    }

    suspend fun deleteMessages(vararg messages: ChatMessageBean) {
        chatGPTDao.deleteMessages(*messages)
    }
}