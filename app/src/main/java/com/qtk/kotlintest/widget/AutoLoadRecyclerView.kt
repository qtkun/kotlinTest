package com.qtk.kotlintest.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AutoLoadRecyclerView : RecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    init {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    SCROLL_STATE_IDLE -> Glide.with(context).resumeRequests()
                    SCROLL_STATE_DRAGGING -> Glide.with(context).pauseRequests()
                    SCROLL_STATE_SETTLING -> Glide.with(context).pauseRequests()
                }
            }
        })
    }
}