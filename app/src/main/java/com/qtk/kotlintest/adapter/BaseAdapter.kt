package com.qtk.kotlintest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.extensions.ctx
import kotlinx.android.extensions.LayoutContainer

abstract class BaseAdapter<VH : BaseViewHolder<T>, T : Any>(
    var items: List<T>?,
    private val itemClick: (T) -> Unit,
    val id: Int
) : RecyclerView.Adapter<VH>() {

    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bindView(items?.get(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.ctx).inflate(id, parent, false)
        return initViewHolder(view, itemClick)
    }

    abstract fun initViewHolder(view: View, itemClick: (T) -> Unit): VH
}

abstract class BaseViewHolder<T : Any>(
    override val containerView: View,
    private val itemClick: (T) -> Unit
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bindView(t: T?) {
        t?.let {
            bind(it)
            itemView.setOnClickListener { itemClick(t) }
        }
    }

    abstract fun bind(t: T)
}

fun <VH : BaseViewHolder<T>, T : Any> BaseAdapter<VH, T>.update(newItems: List<T>) {
    items = newItems
    notifyDataSetChanged()
}

inline fun <VH : RecyclerView.ViewHolder>RecyclerView.Adapter<VH>.update (up: () -> Unit){
    up()
    notifyDataSetChanged()
}