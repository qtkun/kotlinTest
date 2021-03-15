package com.qtk.kotlintest.extensions

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

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

fun makePopupWindowMeasureSpec(measureSpec: Int): Int{
    val model = if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
        View.MeasureSpec.UNSPECIFIED
    } else {
        View.MeasureSpec.EXACTLY
    }
    return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), model)
}