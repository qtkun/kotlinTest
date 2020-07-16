package com.qtk.kotlintest.extensions

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable

val View.ctx : Context
    get() = context

var TextView.textColor : Int
    get() = currentTextColor
    set(value) = setTextColor(value)

var TextView.textSize : Float
    get() = textSize
    set(value) = setTextSize(TypedValue.COMPLEX_UNIT_SP, value)

fun View.slideExit() {
    if (translationY == 0f) animate().translationY(-height.toFloat())
}

fun View.slideEnter() {
    if (translationY < 0f) animate().translationY(0f)
}