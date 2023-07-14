package com.qtk.kotlintest.adapter

import com.qtk.kotlintest.base.base.AdapterProxy
import com.qtk.kotlintest.base.base.BaseViewHolder
import com.qtk.kotlintest.databinding.ItemListVideoBinding
import com.qtk.kotlintest.extensions.singleClick
import com.qtk.kotlintest.preload.PreloadManager
import com.shuyu.gsyvideoplayer.GSYVideoManager

class VideoListAdapterProxy: AdapterProxy<String, ItemListVideoBinding>() {
    companion object {
        const val TAG = "VideoListAdapter"
    }
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemListVideoBinding>,
        item: String,
        position: Int
    ) {
        with(holder.binding) {
            PreloadManager.instance.addPreloadTask(item, position)
            root.tag = item
            pauseView.singleClick {
                if (GSYVideoManager.instance().isPlaying) {
                    GSYVideoManager.onPause()
                } else {
                    GSYVideoManager.onResume(false)
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<ItemListVideoBinding>) {
        super.onViewDetachedFromWindow(holder)
        (holder.binding.root.tag as? String)?.let {
            PreloadManager.instance.removePreloadTask(it)
        }
    }

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}