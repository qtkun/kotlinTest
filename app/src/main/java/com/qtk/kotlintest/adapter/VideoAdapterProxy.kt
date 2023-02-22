package com.qtk.kotlintest.adapter

import androidx.core.view.isVisible
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.base.AdapterProxy
import com.qtk.kotlintest.base.base.BaseViewHolder
import com.qtk.kotlintest.databinding.ItemVideoBinding

class VideoAdapterProxy: AdapterProxy<String, ItemVideoBinding>() {
    companion object {
        const val TAG = "VideoAdapter"
    }
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemVideoBinding>,
        item: String,
        position: Int
    ) {
        holder.binding.detailPlayer.apply {
            setUpLazy(item, true, null, null, "Video $position")
            setThumbPlay(true)
            shrinkImageRes = R.drawable.ic_icon_shrink
            enlargeImageRes = R.drawable.ic_icon_enlarge
            titleTextView.isVisible = true
            titleTextView.text = "Video $position"
            backButton.isVisible = false
            fullscreenButton.setOnClickListener {
                startWindowFullscreen(context, false, true)
            }
            playTag = TAG
            playPosition = position
            isAutoFullWithSize = true
            isFullHideStatusBar = true
            isNeedAutoAdaptation = true
            isReleaseWhenLossAudio = false
            setIsTouchWiget(false)
            isRotateWithSystem = false
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder<ItemVideoBinding>) {
        holder.binding.detailPlayer.release()
    }

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}