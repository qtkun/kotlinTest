package com.qtk.kotlintest.widget

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.*
import androidx.lifecycle.Lifecycle.Event.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AutoLoadRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    init {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    SCROLL_STATE_IDLE -> {
                        if (getLifecycleOwner()?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                            Glide.with(context).resumeRequests()
                        }
                    }
                    SCROLL_STATE_DRAGGING -> {
                        if (getLifecycleOwner()?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                            Glide.with(context).pauseRequests()
                        }
                    }
                    SCROLL_STATE_SETTLING -> {
                        if (getLifecycleOwner()?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                            Glide.with(context).pauseRequests()
                        }
                    }
                }
            }
        })
    }

    private fun getLifecycleOwner(): LifecycleOwner? {
        if (context is LifecycleOwner) {
            return context as LifecycleOwner
        }
        return null
    }
}