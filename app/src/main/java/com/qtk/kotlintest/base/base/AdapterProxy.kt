package com.qtk.kotlintest.base.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class AdapterProxy<T, in VB: ViewBinding>: DiffUtil.ItemCallback<T>() {
    fun onCreateViewHolder(
        parent: ViewGroup,
        itemClick: ((Int) -> Unit)? = null,
        itemLongClick: ((Int) -> Unit)? = null
    ): RecyclerView.ViewHolder {
        val binding = (javaClass.genericSuperclass as ParameterizedType).let {
            it.actualTypeArguments.let { types ->
                (types[1] as Class<VB>).getMethod("inflate", LayoutInflater::class.java,
                    ViewGroup::class.java, Boolean::class.java).invoke(null,
                    LayoutInflater.from(parent.context), parent, false) as VB
            }
        }
        return BaseViewHolder(binding).apply {
            binding.root.setOnClickListener {
                itemClick?.invoke(layoutPosition)
            }
            binding.root.setOnLongClickListener {
                itemLongClick?.let {
                    it.invoke(layoutPosition)
                    return@setOnLongClickListener false
                }
                true
            }
        }
    }

    abstract fun onBindViewHolder(holder: BaseViewHolder<VB>, item: T, position: Int)

    open fun onBindViewHolder(holder: BaseViewHolder<VB>, item: T, position: Int, payloads: MutableList<Any>) {
        onBindViewHolder(holder, item, position)
    }

    open fun onViewRecycled(holder: BaseViewHolder<VB>) = Unit

    open fun onViewDetachedFromWindow(holder: BaseViewHolder<VB>) = Unit
}