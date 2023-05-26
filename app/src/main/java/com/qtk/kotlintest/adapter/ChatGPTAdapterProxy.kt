package com.qtk.kotlintest.adapter

import com.qtk.kotlintest.base.base.AdapterProxy
import com.qtk.kotlintest.base.base.BaseViewHolder
import com.qtk.kotlintest.databinding.ItemChatgptMessageBinding
import com.qtk.kotlintest.room.entity.ChatMessageBean

class ChatGPTAdapterProxy: AdapterProxy<ChatMessageBean, ItemChatgptMessageBinding>() {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemChatgptMessageBinding>,
        item: ChatMessageBean,
        position: Int
    ) {
        with(holder.binding) {
            tvContent.text = item.content
        }
    }

    override fun areItemsTheSame(oldItem: ChatMessageBean, newItem: ChatMessageBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessageBean, newItem: ChatMessageBean): Boolean {
        return oldItem.id == newItem.id
    }
}