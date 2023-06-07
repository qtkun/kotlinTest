package com.qtk.kotlintest.view_model

import androidx.lifecycle.viewModelScope
import com.qtk.kotlintest.base.base.BaseViewModel
import com.qtk.kotlintest.domain.usercase.GetHistoryMessageUserCase
import com.qtk.kotlintest.domain.usercase.SaveUserMessageUserCase
import com.qtk.kotlintest.domain.usercase.SendMessageAndSaveUserCase
import com.qtk.kotlintest.repository.ChatGPTRepository
import com.qtk.kotlintest.room.entity.ChatMessageBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatGPTViewModel @Inject constructor(
    private val chatGPTRepository: ChatGPTRepository,
    private val getHistoryMessageUserCase: GetHistoryMessageUserCase,
    private val sendMessageAndSaveUserCase: SendMessageAndSaveUserCase,
    private val saveUserMessageUserCase: SaveUserMessageUserCase
) : BaseViewModel() {
    val message = MutableStateFlow<Any?>(null)

    val historyMessage = MutableStateFlow<List<Any>>(emptyList())

    fun sendMessageToChatGPT(messageList: List<Any>) = viewModelScope.launch {
        sendMessageAndSaveUserCase(messageList)
            .baseLoading()
            .collect {
                message.value = it
            }
    }

    fun getMessageFromRoom() = viewModelScope.launch {
        getHistoryMessageUserCase()
            .baseLoading()
            .collect {
                historyMessage.value = it
            }
    }

    fun saveUserMessage(content: String) = viewModelScope.launch {
        message.value = saveUserMessageUserCase(content).first()
    }

    fun deleteAllMessages() = viewModelScope.launch {
        chatGPTRepository.deleteAllMessages()
    }

    fun deleteMessage(vararg messages: ChatMessageBean) = viewModelScope.launch {
        chatGPTRepository.deleteMessages(*messages)
    }
}