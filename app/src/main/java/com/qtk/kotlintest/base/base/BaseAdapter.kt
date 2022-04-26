package com.qtk.kotlintest.base.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseAdapter<T: Any, VB: ViewBinding>(
    val items: MutableList<T>,
    private val itemClick: ((Int, T) -> Unit)? = null,
    private val itemLongClick: ((Int, T) -> Unit)? = null,
): RecyclerView.Adapter<BaseViewHolder<T, VB>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, VB> {
        val binding = (javaClass.genericSuperclass as ParameterizedType).let {
            it.actualTypeArguments.let { types ->
                (types[1] as Class<VB>).getMethod("inflate", LayoutInflater::class.java,
                    ViewGroup::class.java, Boolean::class.java).invoke(null,
                    LayoutInflater.from(parent.context), parent, false) as VB
            }
        }
        return BaseViewHolder(binding, itemClick, itemLongClick)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T, VB>, position: Int) {
        holder.bind(position, items[position]).bindView(position, items[position])
    }

    override fun getItemCount(): Int = items.size

    val lastIndex: Int
        get() = items.lastIndex

    protected abstract fun VB.bindView(position: Int, item: T)
}

abstract class BaseListAdapter<T: Any, VB: ViewBinding>(
    private val itemClick: ((Int, T) -> Unit)? = null,
    private val itemLongClick: ((Int, T) -> Unit)? = null,
    diffUtil: DiffUtil.ItemCallback<T> = DiffUtilHelper.create()
): ListAdapter<T, BaseViewHolder<T, VB>>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, VB> {
        val binding = (javaClass.genericSuperclass as ParameterizedType).let {
            it.actualTypeArguments.let { types ->
                (types[1] as Class<VB>).getMethod("inflate", LayoutInflater::class.java,
                    ViewGroup::class.java, Boolean::class.java).invoke(null,
                    LayoutInflater.from(parent.context), parent, false) as VB
            }
        }
        return BaseViewHolder(binding, itemClick, itemLongClick)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T, VB>, position: Int) {
        holder.bind(position, getItem(position)).bindView(position, getItem(position))
    }

    val lastIndex: Int
        get() = itemCount - 1

    protected abstract fun VB.bindView(position: Int, item: T)
}

open class BaseViewHolder<T: Any, VB: ViewBinding>(
    val binding: VB,
    private val itemClick: ((Int, T) -> Unit)? = null,
    private val itemLongClick: ((Int, T) -> Unit)? = null,
): RecyclerView.ViewHolder(binding.root) {
    open fun bind(position: Int, item: T): VB {
        binding.root.setOnClickListener {
            itemClick?.invoke(position, item)
        }
        binding.root.setOnLongClickListener {
            itemLongClick?.let {
                it.invoke(position, item)
                return@setOnLongClickListener false
            }
            true
        }
        return binding
    }
}

open class DiffUtilHelper<T> : DiffUtil.ItemCallback<T>() {
    companion object {
        fun <T> create(): DiffUtilHelper<T> = DiffUtilHelper()
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem === newItem

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem.toString() == newItem.toString()

}