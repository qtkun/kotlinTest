package com.qtk.kotlintest.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qtk.kotlintest.room.entity.ChatMessageBean

@Dao
interface ChatGPTDao {
    @Query("select * from chat_message")
    suspend fun getChatMessageList(): List<ChatMessageBean>?

    @Query("select * from chat_message where id = :id")
    suspend fun getChatMessage(id: String): List<ChatMessageBean>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(messageBean: ChatMessageBean)

    @Delete
    suspend fun deleteMessages(vararg messages: ChatMessageBean)

    @Query("delete from chat_message")
    suspend fun deleteAllMessages()
}