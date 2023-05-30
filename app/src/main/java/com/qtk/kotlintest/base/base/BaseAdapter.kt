package com.qtk.kotlintest.base.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.qtk.kotlintest.R
import java.lang.IllegalArgumentException
import java.lang.reflect.ParameterizedType

class MultiAdapter(
    private val proxies: List<AdapterProxy<*, *>>,
    private val itemClick: ((Int) -> Unit)? = null,
    private val itemLongClick: ((Int) -> Unit)? = null,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items: MutableList<Any> = mutableListOf()
    companion object {
        private const val TYPE_EMPTY = -2
        private const val TYPE_UNKNOWN = -1
    }

    private var emptyView: View? = null

    fun setEmptyView(view: View) {
        emptyView = view
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 else items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()) TYPE_EMPTY else proxies.getProxyIndex(items[position])
    }

    val data: List<Any> get() = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_EMPTY) {
            object : RecyclerView.ViewHolder(emptyView ?: LayoutInflater.from(parent.context).inflate(
                R.layout.empty_view, parent, false)) {}
        } else if(viewType != TYPE_UNKNOWN) {
            proxies[viewType].onCreateViewHolder(parent, itemClick, itemLongClick)
        } else {
            throw IllegalArgumentException("缺少数据模型对应Proxy")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItemViewType(position).let { viewType ->
            if (viewType != TYPE_EMPTY && viewType != TYPE_UNKNOWN) {
                (proxies[getItemViewType(position)] as AdapterProxy<Any, ViewBinding>).onBindViewHolder(
                    holder as BaseViewHolder<ViewBinding>, items[position], position)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        getItemViewType(position).let { viewType ->
            if (viewType != TYPE_EMPTY && viewType != TYPE_UNKNOWN) {
                (proxies[getItemViewType(position)] as AdapterProxy<Any, ViewBinding>).onBindViewHolder(
                    holder as BaseViewHolder<ViewBinding>, items[position], position, payloads)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder.itemViewType != TYPE_EMPTY && holder.itemViewType != TYPE_UNKNOWN) {
            (proxies[holder.itemViewType] as AdapterProxy<Any, ViewBinding>).onViewRecycled(holder as BaseViewHolder<ViewBinding>)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (holder.itemViewType != TYPE_EMPTY && holder.itemViewType != TYPE_UNKNOWN) {
            (proxies[holder.itemViewType] as AdapterProxy<Any, ViewBinding>).onViewDetachedFromWindow(holder as BaseViewHolder<ViewBinding>)
        }
    }

    val lastIndex: Int
        get() = items.lastIndex

    val size: Int
        get() = items.size

    operator fun get(position: Int): Any {
        return items[position]
    }

    fun setData(data: List<Any>) {
        items.clear()
        items.addAll(data)
        notifyItemRangeChanged(0, data.size)
    }

    fun addData(data: List<Any>) {
        val positionStart = items.size
        items.addAll(data)
        notifyItemRangeInserted(positionStart, data.size)
    }

    fun addData(data: Any) {
        items.add(data)
        notifyItemInserted(items.size)
    }

    fun remove(data: Any) {
        val position = items.indexOf(data)
        removeAt(position)
    }

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun remove(data: List<Any>) {
        for (datum in data) {
            remove(datum)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAll() {
        items.clear()
        notifyDataSetChanged()
    }

    fun update(data: Any) {
        for ((index, item) in items.withIndex()) {
            if ((proxies[getItemViewType(index)] as AdapterProxy<Any, ViewBinding>).areItemsTheSame(data, item)) {
                items[index] = data
                notifyItemChanged(index)
                break
            }
        }
    }
}

class MultiTypeListAdapter(
    private val proxies: List<AdapterProxy<*, *>>,
    private val itemClick: ((Int) -> Unit)? = null,
    private val itemLongClick: ((Int) -> Unit)? = null
): ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback(proxies)) {
    companion object {
        private const val TYPE_EMPTY = -2
        private const val TYPE_UNKNOWN = -1
    }

    private var emptyView: View? = null

    fun setEmptyView(view: View) {
        emptyView = view
    }

    override fun getItemCount(): Int {
        return if (currentList.isEmpty()) 1 else super.getItemCount()
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList.isEmpty()) TYPE_EMPTY else proxies.getProxyIndex(currentList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_EMPTY) {
            object : RecyclerView.ViewHolder(emptyView ?: LayoutInflater.from(parent.context).inflate(
                R.layout.empty_view, parent, false)) {}
        } else if(viewType != -1) {
            proxies[viewType].onCreateViewHolder(parent, itemClick, itemLongClick)
        } else {
            throw IllegalArgumentException("缺少数据模型对应Proxy")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItemViewType(position).let { viewType ->
            if (viewType != TYPE_EMPTY && viewType != TYPE_UNKNOWN) {
                (proxies[getItemViewType(position)] as AdapterProxy<Any, ViewBinding>).onBindViewHolder(
                    holder as BaseViewHolder<ViewBinding>, getItem(position), position)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        getItemViewType(position).let { viewType ->
            if (viewType != TYPE_EMPTY && viewType != TYPE_UNKNOWN) {
                (proxies[getItemViewType(position)] as AdapterProxy<Any, ViewBinding>).onBindViewHolder(
                    holder as BaseViewHolder<ViewBinding>, getItem(position), position, payloads)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder.itemViewType != TYPE_EMPTY && holder.itemViewType != TYPE_UNKNOWN) {
            (proxies[holder.itemViewType] as AdapterProxy<Any, ViewBinding>).onViewRecycled(holder as BaseViewHolder<ViewBinding>)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (holder.itemViewType != TYPE_EMPTY && holder.itemViewType != TYPE_UNKNOWN) {
            (proxies[holder.itemViewType] as AdapterProxy<Any, ViewBinding>).onViewDetachedFromWindow(holder as BaseViewHolder<ViewBinding>)
        }
    }

    val lastIndex: Int
        get() = itemCount - 1

}

open class BaseViewHolder<out VB: ViewBinding>(
    private val _binding: VB
): RecyclerView.ViewHolder(_binding.root) {
    val binding: VB get() = _binding
}

fun List<AdapterProxy<*, *>>.getProxyIndex(data: Any): Int = indexOfFirst {
    // 如果AdapterProxy<T: Any, VB: ViewBinding>中的第一个类型参数T和数据的类型相同，则返回对应策略的索引
    (it.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0].toString() == data.javaClass.toString()
}

internal class DiffCallback(
    private val proxies: List<AdapterProxy<*, *>>,
): DiffUtil.ItemCallback<Any>() {

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        val proxy = proxies.getProxyIndex(oldItem)
        return if (proxy >= 0) (proxies[proxy] as AdapterProxy<Any, ViewBinding>).areItemsTheSame(oldItem, newItem) else false
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        val proxy = proxies.getProxyIndex(oldItem)
        return if (proxy >= 0) (proxies[proxy] as AdapterProxy<Any, ViewBinding>).areContentsTheSame(oldItem, newItem) else false
    }

}