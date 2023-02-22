package com.qtk.kotlintest.adapter

import com.bumptech.glide.Glide
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.base.AdapterProxy
import com.qtk.kotlintest.base.base.BaseViewHolder
import com.qtk.kotlintest.databinding.ItemPokemon2Binding
import com.qtk.kotlintest.retrofit.data.PokemonBean

class PokemonAdapterProxy: AdapterProxy<PokemonBean, ItemPokemon2Binding>() {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemPokemon2Binding>,
        item: PokemonBean,
        position: Int
    ) {
        with(holder.binding) {
            Glide.with(iv)
                .load(item.url)
                .placeholder(R.drawable.ic_place_holder)
                .override(300, 300)
                .into(iv)
            root.isSelected = item.like
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder<ItemPokemon2Binding>) {
        Glide.with(holder.binding.root.context).clear(holder.binding.iv)
    }

    override fun areItemsTheSame(oldItem: PokemonBean, newItem: PokemonBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PokemonBean, newItem: PokemonBean): Boolean {
        return oldItem.toString() == newItem.toString()
    }

}