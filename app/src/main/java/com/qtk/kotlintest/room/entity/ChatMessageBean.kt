package com.qtk.kotlintest.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_message")
data class ChatMessageBean(
    @PrimaryKey val id: String = "",
    val content: String,
    val role: String
)

object Role {
    const val ASSISTANT = "assistant"
    const val USER = "user"
}