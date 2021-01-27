package com.qtk.kotlintest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.ItemLoadMoreBinding
import com.qtk.kotlintest.extensions.ctx
import kotlinx.android.extensions.LayoutContainer

class LoadMoreAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadMoreView>() {
    override fun onBindViewHolder(holder: LoadMoreView, loadState: LoadState) {
        holder.bindState(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadMoreView {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.item_load_more, parent, false)
        return LoadMoreView(view) { retry }
    }
}

class LoadMoreView(
    override val containerView: View,
    private val retry: () -> Unit
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    private val binding = ItemLoadMoreBinding.bind(containerView)
    private val loading = ObservableBoolean()

    fun bindState(loadState: LoadState) {
        loading.set(loadState is LoadState.Loading)
        binding.loadingProgress.visibility = if (loading.get()) View.VISIBLE else View.GONE
        binding.btnReload.visibility = if (loading.get()) View.GONE else View.VISIBLE
        binding.btnReload.setOnClickListener { retry }
    }
}
