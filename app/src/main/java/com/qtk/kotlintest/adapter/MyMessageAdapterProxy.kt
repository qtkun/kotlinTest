package com.qtk.kotlintest.adapter

import com.qtk.kotlintest.base.base.AdapterProxy
import com.qtk.kotlintest.base.base.BaseViewHolder
import com.qtk.kotlintest.databinding.ItemMyMessageBinding

class MyMessageAdapterProxy: AdapterProxy<String, ItemMyMessageBinding>() {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemMyMessageBinding>,
        item: String,
        position: Int
    ) {
        with(holder.binding) {
            tvContent.text = item
        }
    }

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}