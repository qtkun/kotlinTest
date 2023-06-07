package com.qtk.kotlintest.modules

import com.qtk.kotlintest.domain.usercase.GetHistoryMessageUserCase
import com.qtk.kotlintest.domain.usercase.SaveUserMessageUserCase
import com.qtk.kotlintest.domain.usercase.SendMessageAndSaveUserCase
import com.qtk.kotlintest.repository.ChatGPTRepository
import com.qtk.kotlintest.retrofit.data.ApiResult
import com.qtk.kotlintest.room.entity.ChatMessageBean
import com.qtk.kotlintest.room.entity.Role
import com.qtk.kotlintest.room.entity.UserMessageBean
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.UUID

@Module
@InstallIn(ViewModelComponent::class)
object UserCaseModule {
    @Provides
    fun providerGetHistoryMessageUserCase(chatGPTRepository: ChatGPTRepository) = GetHistoryMessageUserCase {
        chatGPTRepository.getMessageFromRoom()
    }

    @Provides
    fun providerSendMessageAndSaveUserCase(chatGPTRepository: ChatGPTRepository) = SendMessageAndSaveUserCase { content ->
        chatGPTRepository.sendMessageToChatGPT(content)
            .map {
                if (it is ApiResult.Success) {
                    it.data?.let { chatBean ->
                        var receiveContent = ""
                        var role = ""
                        if (chatBean.choices.isNotEmpty()) {
                            receiveContent = chatBean.choices.first().message?.content ?: ""
                            role = chatBean.choices.first().message?.role ?: Role.ASSISTANT
                        }
                        val messageBean = ChatMessageBean(chatBean.id, receiveContent, role)
                        chatGPTRepository.insertMessage(messageBean)
                        messageBean
                    }
                } else null
            }
    }

    @Provides
    fun providerSaveUserMessageUserCase(chatGPTRepository: ChatGPTRepository) = SaveUserMessageUserCase { content ->
        flow {
            val message = UserMessageBean(UUID.randomUUID().toString(), content, Role.USER)
            emit(message)
            chatGPTRepository.insertMessage(message.mapToChatMessage())
        }.flowOn(Dispatchers.IO)
    }
}