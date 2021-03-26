package com.qtk.kotlintest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.qtk.kotlintest.R
import com.qtk.kotlintest.retrofit.data.PokemonBean
import com.qtk.kotlintest.base.BaseViewHolder
import com.qtk.kotlintest.base.DiffUtilHelper
import com.qtk.kotlintest.databinding.ItemPokemonBinding
import com.qtk.kotlintest.domain.model.Forecast

class PokemonAdapter(private val itemClick : (PokemonBean, ItemPokemonBinding) -> Unit) :
    PagingDataAdapter<PokemonBean, BaseViewHolder<PokemonBean, ItemPokemonBinding>>(object : DiffUtilHelper<PokemonBean>() {
        override fun areItemsTheSame(oldItem: PokemonBean, newItem: PokemonBean): Boolean = oldItem.id == newItem.id
    }) {

    override fun onBindViewHolder(holder: BaseViewHolder<PokemonBean, ItemPokemonBinding>, position: Int) {
        holder.bindView(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PokemonBean, ItemPokemonBinding> {
        val binding: ItemPokemonBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_pokemon, parent, false)
        return BaseViewHolder(binding, itemClick)
    }
}