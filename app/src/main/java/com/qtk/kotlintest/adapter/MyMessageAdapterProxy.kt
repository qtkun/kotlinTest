package com.qtk.kotlintest.adapter

import com.qtk.kotlintest.base.base.AdapterProxy
import com.qtk.kotlintest.base.base.BaseViewHolder
import com.qtk.kotlintest.databinding.ItemMyMessageBinding
import com.qtk.kotlintest.extensions.TextMenuItemOnClickListener
import com.qtk.kotlintest.extensions.setSelectionMenu
import com.qtk.kotlintest.room.entity.UserMessageBean

class MyMessageAdapterProxy(private val onMessageDelete: (Int) -> Unit): AdapterProxy<UserMessageBean, ItemMyMessageBinding>() {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemMyMessageBinding>,
        item: UserMessageBean,
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

    override fun areItemsTheSame(oldItem: UserMessageBean, newItem: UserMessageBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserMessageBean, newItem: UserMessageBean): Boolean {
        return oldItem.content == newItem.content
    }
}