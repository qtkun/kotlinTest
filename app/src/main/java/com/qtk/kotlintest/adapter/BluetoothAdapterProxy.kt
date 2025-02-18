package com.qtk.kotlintest.adapter

import com.qtk.kotlintest.base.base.AdapterProxy
import com.qtk.kotlintest.base.base.BaseViewHolder
import com.qtk.kotlintest.databinding.ItemBluetoothBinding

class BluetoothAdapterProxy: AdapterProxy<String, ItemBluetoothBinding>() {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemBluetoothBinding>,
        item: String,
        position: Int
    ) {
        holder.binding.tvName.text = item
    }

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}