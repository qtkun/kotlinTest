package com.qtk.kotlintest.adapter

import com.qtk.kotlintest.base.base.AdapterProxy
import com.qtk.kotlintest.base.base.BaseViewHolder
import com.qtk.kotlintest.databinding.ItemPictureBinding

class PictureAdapterProxy: AdapterProxy<String, ItemPictureBinding>() {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemPictureBinding>,
        item: String,
        position: Int
    ) {
        with(holder.binding) {
            zoomIv.setUrl(item)
        }
    }

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}