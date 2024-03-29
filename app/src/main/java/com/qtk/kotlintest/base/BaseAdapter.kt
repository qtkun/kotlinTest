package com.qtk.kotlintest.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.BR
import com.qtk.kotlintest.extensions.singleClick
import com.qtk.kotlintest.extensions.toJson
import com.squareup.moshi.Moshi

abstract class BaseAdapter<T : Any, VDB: ViewDataBinding>(
    var items: List<T>?,
    private val itemClick: (T, VDB) -> Unit,
    val id: Int
) : RecyclerView.Adapter<BaseViewHolder<T, VDB>>() {

    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(holder: BaseViewHolder<T, VDB>, position: Int) {
        items?.let {
            holder.bindView(it[position])
            onBind(holder.binding, it[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, VDB> {
        val binding: VDB =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), id, parent, false)
        return BaseViewHolder(binding, itemClick)
    }

    open fun onBind(binding: VDB, item: T) {}
}

abstract class BaseListAdapter<T : Any, VDB: ViewDataBinding>(
    var items: List<T>?,
    private val itemClick: (T, VDB) -> Unit,
    val id: Int,
    diffUtil: DiffUtil.ItemCallback<T> = DiffUtilHelper.create()
) : ListAdapter<T, BaseViewHolder<T, VDB>>(diffUtil) {
    override fun onBindViewHolder(holder: BaseViewHolder<T, VDB>, position: Int) {
        holder.bindView(getItem(position))
        onBind(holder.binding, getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, VDB> {
        val binding: VDB =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), id, parent, false)
        return BaseViewHolder(binding, itemClick)
    }

    open fun onBind(binding: VDB, item: T) {}
}

open class DiffUtilHelper<T> : DiffUtil.ItemCallback<T>() {
    companion object {
        fun <T> create(): DiffUtilHelper<T> = DiffUtilHelper()
    }

    override fun areItemsTheSame(oldItem: T & Any, newItem: T & Any): Boolean = oldItem === newItem

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T & Any, newItem: T & Any): Boolean = oldItem == newItem

}

open class DiffCallBack<T>(protected val oldItems: List<T>, protected val newItems: List<T>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldItems[oldItemPosition] === newItems[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldItems[oldItemPosition].toString() == newItems[newItemPosition].toString()

}

class BaseViewHolder<T : Any, VDB: ViewDataBinding>(
    private val _binding: VDB,
    private val itemClick: (T, VDB) -> Unit
) : RecyclerView.ViewHolder(_binding.root) {
    fun bindView(item: T?) {
        item?.let {
            _binding.setVariable(BR.item, item)
            _binding.executePendingBindings()
            itemView.singleClick { itemClick(item, _binding) }
        }
    }

    val binding get() = _binding
}

fun <T : Any, VDB: ViewDataBinding> BaseAdapter<T, VDB>.update(newItems: List<T>, diffUtil: DiffUtil.Callback = DiffCallBack(items ?: emptyList(), newItems)) {
    items = newItems
    val result = DiffUtil.calculateDiff(diffUtil, true)
    result.dispatchUpdatesTo(this)
}

fun <T : Any, VDB: ViewDataBinding> BaseListAdapter<T, VDB>.update(newItems: List<T>) {
    items = newItems
    submitList(items)
}

fun <T : Any, VDB: ViewDataBinding> BaseListAdapter<T, VDB>.add(addItem: T) {
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

fun <T : Any, VDB: ViewDataBinding> BaseListAdapter<T, VDB>.addAll(addItems: List<T>) {
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

fun <T : Any, VDB: ViewDataBinding> BaseListAdapter<T, VDB>.remove(deleteItem: T) {
    items = items?.let {
        ArrayList<T>(it).apply {
            remove(deleteItem)
        }
    }
    submitList(items)
}

fun <T : Any, VDB: ViewDataBinding> BaseListAdapter<T, VDB>.removeAll(deleteItems: List<T>) {
    items = items?.let {
        ArrayList<T>(it).apply {
            removeAll(deleteItems)
        }
    }
    submitList(items)
}

fun <T : Any, VDB: ViewDataBinding> BaseListAdapter<T, VDB>.clear() {
    items = items?.let {
        ArrayList<T>(it).apply {
            clear()
        }
    }
    submitList(items)
}