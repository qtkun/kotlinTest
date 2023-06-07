package com.qtk.kotlintest.domain.usercase

import com.qtk.kotlintest.room.entity.ChatMessageBean
import com.qtk.kotlintest.room.entity.UserMessageBean
import kotlinx.coroutines.flow.Flow

fun interface GetHistoryMessageUserCase : () -> Flow<List<Any>>

fun interface SendMessageAndSaveUserCase: (List<Any>) -> Flow<ChatMessageBean?>

fun interface SaveUserMessageUserCase: (String) -> Flow<UserMessageBean>