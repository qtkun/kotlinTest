package com.qtk.kotlintest.extensions

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd

val View.ctx : Context
    get() = context

var TextView.textColor : Int
    get() = currentTextColor
    set(value) = setTextColor(value)

var TextView.textSize : Float
    get() = textSize
    set(value) = setTextSize(TypedValue.COMPLEX_UNIT_SP, value)

fun View.slideExit() {
    if (translationY == 0f) animate().translationY(-height.toFloat()).duration = 100
}

fun View.slideEnter() {
    if (translationY < 0f) animate().translationY(0f).duration = 100
}

private var View.lastClickTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else 0L
    set(value) {
        setTag(1123460103, value)
    }

fun View.singleClick(duration: Long = 500L, onClick: (View) -> Unit) {
    setOnClickListener {
        val currentClickTime = System.currentTimeMillis()
        if(currentClickTime - lastClickTime > duration) {
            onClick(it)
        }
        lastClickTime = currentClickTime
    }
}

fun View.addAnimView(str: String, dur: Long = 300) {
    val location = IntArray(2)
    getLocationOnScreen(location)
    val startX = location[0] + width / 2f
    val startY = location[1] + height / 2f
    val icon = TextView(context).apply {
        layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        x = startX
        y = startY
        text = str
        scaleX = 2f
        scaleY = 2f
    }
    val root = (context as Activity).findViewById<FrameLayout>(android.R.id.content).apply {
        addView(icon)
    }
    val topX = -(context as Activity).getScreenWidth() * Math.random().toFloat() + startX
    val topY = -(context as Activity).getScreenHeight() / 3f * Math.random().toFloat() + startY
    val translateXUp = ObjectAnimator.ofFloat(icon, "translationX", startX, topX).apply {
        interpolator = LinearInterpolator()
        duration = dur
    }
    val translateYUp = ObjectAnimator.ofFloat(icon, "translationY", startY, topY).apply {
        interpolator = DecelerateInterpolator()
        duration = dur
    }

    val translateX = ObjectAnimator.ofFloat(icon, "translationX", topX, topX * 0.9f).apply {
        interpolator = LinearInterpolator()
        duration = dur
    }
    val translateY = ObjectAnimator.ofFloat(icon, "translationY", topY, topY * 1.1f).apply {
        interpolator = AccelerateInterpolator()
        duration = dur
    }
    val alpha = ObjectAnimator.ofFloat(icon, "alpha", 1f, 0f).apply {
        interpolator = LinearInterpolator()
        duration = dur
        doOnEnd {
            root.removeView(icon)
        }
    }
    AnimatorSet().apply {
        playTogether(translateXUp, translateYUp)
        start()
        doOnEnd {
            AnimatorSet().apply {
                playTogether(translateX, translateY, alpha)
                start()
            }
        }
    }
}