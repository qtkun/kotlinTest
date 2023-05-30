package com.qtk.kotlintest.adapter

import com.qtk.kotlintest.base.base.AdapterProxy
import com.qtk.kotlintest.base.base.BaseViewHolder
import com.qtk.kotlintest.databinding.ItemChatgptMessageBinding
import com.qtk.kotlintest.extensions.TextMenuItemOnClickListener
import com.qtk.kotlintest.extensions.setSelectionMenu
import com.qtk.kotlintest.room.entity.ChatMessageBean

class ChatGPTAdapterProxy(private val onMessageDelete: (Int) -> Unit): AdapterProxy<ChatMessageBean, ItemChatgptMessageBinding>() {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemChatgptMessageBinding>,
        item: ChatMessageBean,
        position: Int
    ) {
        with(holder.binding) {
            tvContent.text = item.content
            tvContent.setSelectionMenu(object: TextMenuItemOnClickListener {
                override fun onMessageDelete() {
                    onMessageDelete(holder.bindingAdapterPosition)
                }
            })
        }
    }

    override fun areItemsTheSame(oldItem: ChatMessageBean, newItem: ChatMessageBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessageBean, newItem: ChatMessageBean): Boolean {
        return oldItem.id == newItem.id
    }
}