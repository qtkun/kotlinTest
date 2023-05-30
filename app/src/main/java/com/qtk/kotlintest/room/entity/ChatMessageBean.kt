package com.qtk.kotlintest.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "chat_message")
data class ChatMessageBean(
    @PrimaryKey val id: String = "",
    val content: String,
    val role: String
) {
    fun mapToUserMessage(): UserMessageBean {
        return UserMessageBean(id, content, role)
    }
}

data class UserMessageBean(
    val id: String = "",
    val content: String,
    val role: String
) {
    fun mapToChatMessage(): ChatMessageBean {
        return ChatMessageBean(id, content, role)
    }
}

object Role {
    const val ASSISTANT = "assistant"
    const val USER = "user"
}