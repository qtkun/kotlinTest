package com.qtk.kotlintest.widget

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller

class TopSmoothScroller(context: Context): LinearSmoothScroller(context) {
    override fun getHorizontalSnapPreference(): Int = SNAP_TO_START

    override fun getVerticalSnapPreference(): Int = SNAP_TO_START


}

fun LinearLayoutManager.smoothScroll(context: Context, position: Int) {
    val topSmoothScroller = TopSmoothScroller(context)
    topSmoothScroller.targetPosition = position
    startSmoothScroll(topSmoothScroller)
}