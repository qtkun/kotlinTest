package com.qtk.kotlintest.extensions

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import org.jetbrains.anko.ctx

fun Context.color(res : Int) : Int = ContextCompat.getColor(this, res)

fun Context.drawable(res: Int): Drawable? = ContextCompat.getDrawable(this, res)

fun Context.string(res: Int): String = this.getString(res)

fun Activity.getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        displayMetrics.widthPixels
    } else {
        windowManager.currentWindowMetrics.bounds.width()
    }
}

fun Activity.getScreenHeight(): Int {
    val displayMetrics = DisplayMetrics()
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        displayMetrics.heightPixels
    } else {
        windowManager.currentWindowMetrics.bounds.height()
    }
}