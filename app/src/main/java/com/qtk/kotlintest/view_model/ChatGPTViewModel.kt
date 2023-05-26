package com.qtk.kotlintest.view_model

import androidx.lifecycle.viewModelScope
import com.qtk.kotlintest.base.base.BaseViewModel
import com.qtk.kotlintest.paging.CommonRepository
import com.qtk.kotlintest.retrofit.data.ApiResult
import com.qtk.kotlintest.retrofit.data.ChatBean
import com.qtk.kotlintest.room.ChatGPTDao
import com.qtk.kotlintest.room.entity.ChatMessageBean
import com.qtk.kotlintest.room.entity.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatGPTViewModel @Inject constructor(
    private val commonRepository: CommonRepository
) : BaseViewModel() {
    val message = MutableStateFlow<ChatMessageBean?>(null)

    val historyMessage = MutableStateFlow<List<Any>>(emptyList())

    fun sendMessageToChatGPT(content: String) = viewModelScope.launch {
        commonRepository.sendMessageToChatGPT(content)
            .baseLoading()
            .collect {
                if (it is ApiResult.Success) {
                    it.data?.let { chatBean ->
                        var receiveContent = ""
                        var role = ""
                        if (chatBean.choices.isNotEmpty()) {
                            receiveContent = chatBean.choices.first().message.content
                            role = chatBean.choices.first().message.role
                        }
                        val messageBean = ChatMessageBean(chatBean.id, receiveContent, role)
                        message.value = messageBean
                        insertMessage(messageBean)
                    }
                }
            }
    }

    fun getMessageFromRoom() = viewModelScope.launch {
        commonRepository.getMessageFromRoom()
            .baseLoading()
            .collect {
                historyMessage.value = it
            }
    }

    fun insertMessage(content: String) = viewModelScope.launch(Dispatchers.IO) {
        commonRepository.insertMessage(ChatMessageBean(UUID.randomUUID().toString(), content, Role.USER))
    }

    private fun insertMessage(messageBean: ChatMessageBean) = viewModelScope.launch(Dispatchers.IO) {
        commonRepository.insertMessage(messageBean)
    }
}