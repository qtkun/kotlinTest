package com.qtk.kotlintest.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.ItemPokemonBinding
import com.qtk.kotlintest.extensions.ctx
import com.qtk.kotlintest.retrofit.data.PokemonBean
import com.qtk.kotlintest.utils.CircleCornerForm
import com.squareup.picasso.Picasso

class PokemonAdapter(private val itemClick : (PokemonBean) -> Unit) :
    PagingDataAdapter<PokemonBean, ViewHolder>(object : DiffUtil.ItemCallback<PokemonBean>(){
        override fun areItemsTheSame(oldItem: PokemonBean, newItem: PokemonBean): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PokemonBean, newItem: PokemonBean): Boolean {
            return oldItem.name == newItem.name
        }

    }) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.item_pokemon, parent, false)
        return ViewHolder(view, itemClick)
    }
}

@SuppressLint("SetTextI18n")
class ViewHolder(containerView: View, itemClick: (PokemonBean) -> Unit)
    : BaseViewHolder<PokemonBean>(containerView, itemClick) {
    private val binding = ItemPokemonBinding.bind(containerView)
    override fun bind(t: PokemonBean) {
        with(t){
            Picasso.get().load(url).transform(CircleCornerForm()).into(binding.icon)
            binding.titleTv.text = name
        }
    }

}