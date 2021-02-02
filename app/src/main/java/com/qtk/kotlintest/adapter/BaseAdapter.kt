package com.qtk.kotlintest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
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

abstract class BaseListAdapter<VH : BaseViewHolder<T>, T : Any>(
    var items: List<T>?,
    private val itemClick: (T) -> Unit,
    val id: Int,
    diffUtil: DiffUtil.ItemCallback<T> = DiffUtilHelper.create()
): ListAdapter<T, VH>(diffUtil) {
    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bindView(items?.get(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.ctx).inflate(id, parent, false)
        return initViewHolder(view, itemClick)
    }

    abstract fun initViewHolder(view: View, itemClick: (T) -> Unit): VH
}

open class DiffUtilHelper<T> : DiffUtil.ItemCallback<T>(){
    companion object{
        fun <T>create(): DiffUtilHelper<T> = DiffUtilHelper()
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem === newItem

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem.toString() == newItem.toString()

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

fun <VH : BaseViewHolder<T>, T : Any> BaseListAdapter<VH, T>.update(newItems: List<T>) {
    items = newItems
    submitList(items)
}

fun <VH : BaseViewHolder<T>, T : Any> BaseListAdapter<VH, T>.add(addItem: T) {
    items = if (items == null) {
        ArrayList<T>().apply { add(addItem) }
    } else {
        items?.let {
            ArrayList<T>(it).apply {
                add(addItem)
            }
        }
    }
    submitList(items)
}

fun <VH : BaseViewHolder<T>, T : Any> BaseListAdapter<VH, T>.addAll(addItems: List<T>) {
    items = if (items == null) {
        addItems
    } else {
        items?.let {
            ArrayList<T>(it).apply {
                addAll(addItems)
            }
        }
    }
    submitList(items)
}

fun <VH : BaseViewHolder<T>, T : Any> BaseListAdapter<VH, T>.remove(deleteItem: T) {
    items = items?.let {
        ArrayList<T>(it).apply {
            remove(deleteItem)
        }
    }
    submitList(items)
}

fun <VH : BaseViewHolder<T>, T : Any> BaseListAdapter<VH, T>.removeAll(deleteItems: List<T>) {
    items = items?.let {
        ArrayList<T>(it).apply {
            removeAll(deleteItems)
        }
    }
    submitList(items)
}

fun <VH : BaseViewHolder<T>, T : Any> BaseListAdapter<VH, T>.clear() {
    items = items?.let {
        ArrayList<T>(it).apply {
            clear()
        }
    }
    submitList(items)
}