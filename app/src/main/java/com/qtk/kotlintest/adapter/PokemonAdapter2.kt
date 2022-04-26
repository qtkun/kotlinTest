package com.qtk.kotlintest.adapter

import com.bumptech.glide.Glide
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.base.BaseListAdapter
import com.qtk.kotlintest.databinding.ItemPokemon2Binding
import com.qtk.kotlintest.retrofit.data.PokemonBean

class PokemonAdapter2(itemClick: (Int, PokemonBean) -> Unit): BaseListAdapter<PokemonBean, ItemPokemon2Binding>(itemClick) {
    override fun ItemPokemon2Binding.bindView(position: Int, item: PokemonBean) {
        Glide.with(iv)
            .load(item.url)
            .placeholder(R.drawable.ic_place_holder)
            .override(300, 300)
            .into(iv)
        root.isSelected = item.like
    }
}