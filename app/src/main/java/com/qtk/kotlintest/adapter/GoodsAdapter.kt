package com.qtk.kotlintest.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.ItemGoodsBinding
import com.qtk.kotlintest.extensions.ctx
import com.qtk.kotlintest.retrofit.data.GoodsBean
import com.qtk.kotlintest.utils.CircleCornerForm
import com.squareup.picasso.Picasso

class GoodsAdapter(private val itemClick : (GoodsBean) -> Unit) :
    PagingDataAdapter<GoodsBean, ViewHolder>(object : DiffUtil.ItemCallback<GoodsBean>(){
        override fun areItemsTheSame(oldItem: GoodsBean, newItem: GoodsBean): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: GoodsBean, newItem: GoodsBean): Boolean {
            return oldItem.id == newItem.id
        }

    }) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.item_goods, parent, false)
        return ViewHolder(view, itemClick)
    }
}

@SuppressLint("SetTextI18n")
class ViewHolder(containerView: View, itemClick: (GoodsBean) -> Unit)
    : BaseViewHolder<GoodsBean>(containerView, itemClick) {
    private val binding = ItemGoodsBinding.bind(containerView)
    override fun bind(t: GoodsBean) {
        with(t){
            Picasso.get().load(goodsimage.url).transform(CircleCornerForm()).into(binding.icon)
            binding.titleTv.text = name
            binding.codeTv.text = "编号：$code"
            binding.priceTv.text = "价格：￥$pricemin~￥$pricemax"
            binding.stockTv.text = "库存：$stock"
        }
    }

}