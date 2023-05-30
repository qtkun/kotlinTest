package com.qtk.kotlintest.paging

import androidx.paging.Pager
import androidx.paging.map
import com.qtk.kotlintest.api.Api
import com.qtk.kotlintest.api.ChatGPTApi
import com.qtk.kotlintest.contant.pagingConfig
import com.qtk.kotlintest.retrofit.data.getId
import com.qtk.kotlintest.retrofit.data.getImageUrl
import com.qtk.kotlintest.room.ChatGPTDao
import com.qtk.kotlintest.room.entity.ChatMessageBean
import com.qtk.kotlintest.room.entity.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommonRepository @Inject constructor(
    private val api: Api,
    private val gptApi: ChatGPTApi,
    private val chatGPTDao: ChatGPTDao
) {
    fun getPokemon() = Pager(pagingConfig){
        PokemonDataSource(api)
    }.flow.map {
        it.map { pokemon ->
            pokemon.apply {
                id = getId(pokemon.url)
                url = getImageUrl(pokemon.url)
            }
        }
    }

    fun getPokemon(limit: Int, offset: Int) = flow {
        emit(api.getPokemon(limit, offset))
    }.flowOn(Dispatchers.IO)

    fun sendMessageToChatGPT(content: String) = flow {
        emit(gptApi.sendMessageToChatGPT(content))
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