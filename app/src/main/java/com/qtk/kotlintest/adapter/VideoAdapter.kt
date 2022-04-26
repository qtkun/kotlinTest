package com.qtk.kotlintest.adapter

import androidx.core.view.isVisible
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.base.BaseAdapter
import com.qtk.kotlintest.databinding.ItemVideoBinding
import com.shuyu.gsyvideoplayer.utils.CommonUtil

class VideoAdapter(items: MutableList<String>) : BaseAdapter<String, ItemVideoBinding>(items) {
    val TAG = "VideoAdapter"

    override fun ItemVideoBinding.bindView(position: Int, item: String) {
        detailPlayer.apply {
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
}