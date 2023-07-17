package com.qtk.kotlintest.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.base.base.BaseViewHolder
import com.qtk.kotlintest.databinding.ItemVideoBinding
import com.qtk.kotlintest.widget.DcVideoPlayer
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.NetworkUtils

fun getViewVisiblePercent(view: View?): Float {
    if (view == null) return 0f
    val rect = Rect()
    if (!view.getLocalVisibleRect(rect)) {
        return 0f
    }
    val visibleHeight = rect.bottom - rect.top
    return visibleHeight / view.height.toFloat()
}

fun onScrollReleaseAllVideos(recyclerView: RecyclerView, tag: String, percent: Float) {
    (recyclerView.layoutManager as? LinearLayoutManager)?.apply {
        val firstVisibleItem = findFirstVisibleItemPosition()
        val lastVisibleItem = findLastVisibleItemPosition()
        val position = GSYVideoManager.instance().playPosition
        if (GSYVideoManager.instance().playPosition >= 0) {
            if (GSYVideoManager.instance().playTag == tag &&
                (position <= firstVisibleItem || position >= lastVisibleItem - 1)) {
                if(GSYVideoManager.isFullState(recyclerView.context as Activity)) {
                    return
                }
                val currentView = recyclerView.getChildAt(position - firstVisibleItem)
                if (getViewVisiblePercent(currentView) < percent) {
                    GSYVideoManager.onPause()
                    recyclerView.adapter?.notifyItemChanged(position)
                }
            }
        }
    }
}

fun onScrollPlayVideo(recyclerView: RecyclerView, playerId: Int) {
    if (NetworkUtils.isWifiConnected(recyclerView.context)) {
        (recyclerView.layoutManager as? LinearLayoutManager)?.apply {
            val firstVisibleItem = findFirstVisibleItemPosition()
            val lastVisibleItem = findLastVisibleItemPosition()
            for (position in 0..(lastVisibleItem - firstVisibleItem)) {
                val child = recyclerView.getChildAt(position)
                val view = child?.findViewById<DcVideoPlayer>(playerId)
                if (getViewVisiblePercent(child) == 1f) {
                    if (GSYVideoManager.instance().playPosition != position + firstVisibleItem) {
                        view?.startButton?.performClick()
                    }
                    break
                }
            }
        }
    }
}