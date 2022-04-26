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
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.widget.doOnTextChanged

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

fun EditText.limitDecimal(intLimit: Int = Int.MAX_VALUE, limit: Int = 2) {
    doOnTextChanged { text, _, _, _ ->
        text?.let {
            //如果第一个数字为0，第二个不为点，就不允许输入
            if (text.startsWith("0") && text.toString().trim().length > 1) {
                if (text.substring(1, 2) != ".") {
                    this.setText(text.subSequence(0, 1))
                    setSelection(1)
                    return@doOnTextChanged
                }
            }
            //如果第一为点，直接显示0.
            if (text.startsWith(".")) {
                this.setText("0.")
                setSelection(2)
                return@doOnTextChanged
            }
            if (text.contains(".")) {
                if (text.length - 1 - text.indexOf(".") > limit) {
                    val s = text.subSequence(0, text.indexOf(".") + limit + 1)
                    this.setText(s)
                    setSelection(s.length)
                }
            }
            val split = text.split(".")
            if (split[0].length > intLimit) {
                var s = split[0].substring(0, split[0].length - 1)
                if (split.size > 1) s += ".${split[1]}"
                this.setText(s)
                setSelection(s.length)
            }
        }
    }
}