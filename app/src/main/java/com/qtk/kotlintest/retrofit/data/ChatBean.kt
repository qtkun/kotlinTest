package com.qtk.kotlintest.retrofit.data

data class ChatChunkBean(
    val data: ChatBean
)

data class ChatBean(
    val choices: List<Choice>,
    val created: Long,
    val id: String,
    val `object`: String,
    val usage: Usage?
)

data class Choice(
    val finish_reason: String?,
    val index: Int,
    val message: Message?,
    val delta: Delta?
)

data class Usage(
    val completion_tokens: Int,
    val prompt_tokens: Int,
    val total_tokens: Int
)

data class Message(
    val content: String?,
    val role: String?
)
data class Delta(
    val content: String?,
    val role: String?
)