package com.qtk.kotlintest.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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

inline fun LifecycleOwner.launchOnState(state: Lifecycle.State, crossinline block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(state) {
            block()
        }
    }
}

inline fun <T> Flow<T>.launchAndCollectIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        collect {
            action(it)
        }
    }
}

inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}