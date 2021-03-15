package com.qtk.kotlintest.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.BR
import com.qtk.kotlintest.extensions.singleClick

abstract class BaseAdapter<T : Any>(
    var items: List<T>?,
    private val itemClick: (T) -> Unit,
    val id: Int
) : RecyclerView.Adapter<BaseViewHolder<T>>() {

    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        items?.let {
            onBind(holder.binding, it[position])
            holder.bindView(it[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), id, parent, false)
        return BaseViewHolder(binding, itemClick)
    }

    open fun onBind(binding: ViewDataBinding, item: T) {}
}

abstract class BaseListAdapter<T : Any>(
    var items: List<T>?,
    private val itemClick: (T) -> Unit,
    val id: Int,
    diffUtil: DiffUtil.ItemCallback<T> = DiffUtilHelper.create()
) : ListAdapter<T, BaseViewHolder<T>>(diffUtil) {
    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        items?.let {
            onBind(holder.binding, it[position])
            holder.bindView(it[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), id, parent, false)
        return BaseViewHolder(binding, itemClick)
    }

    open fun onBind(binding: ViewDataBinding, item: T) {}
}

open class DiffUtilHelper<T> : DiffUtil.ItemCallback<T>() {
    companion object {
        fun <T> create(): DiffUtilHelper<T> = DiffUtilHelper()
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem === newItem

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem.toString() == newItem.toString()

}

class BaseViewHolder<T : Any>(
    private val _binding: ViewDataBinding,
    private val itemClick: (T) -> Unit
) : RecyclerView.ViewHolder(_binding.root) {
    fun bindView(item: T?) {
        item?.let {
            _binding.setVariable(BR.item, item)
            _binding.executePendingBindings()
            itemView.singleClick { itemClick(item) }
        }
    }

    val binding get() = _binding
}

fun <T : Any> BaseAdapter<T>.update(newItems: List<T>) {
    items = newItems
    notifyDataSetChanged()
}

fun <T : Any> BaseListAdapter<T>.update(newItems: List<T>) {
    items = newItems
    submitList(items)
}

fun <T : Any> BaseListAdapter<T>.add(addItem: T) {
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

fun <T : Any> BaseListAdapter<T>.addAll(addItems: List<T>) {
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

fun <T : Any> BaseListAdapter<T>.remove(deleteItem: T) {
    items = items?.let {
        ArrayList<T>(it).apply {
            remove(deleteItem)
        }
    }
    submitList(items)
}

fun <T : Any> BaseListAdapter<T>.removeAll(deleteItems: List<T>) {
    items = items?.let {
        ArrayList<T>(it).apply {
            removeAll(deleteItems)
        }
    }
    submitList(items)
}

fun <T : Any> BaseListAdapter<T>.clear() {
    items = items?.let {
        ArrayList<T>(it).apply {
            clear()
        }
    }
    submitList(items)
}