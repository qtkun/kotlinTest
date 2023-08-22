package com.qtk.kotlintest.extensions

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.LayoutDirection
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.text.TextUtilsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withStateAtLeast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Locale

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

fun Window.getScreenWidth(): Int {
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
        repeatOnLifecycle(state) {
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

inline fun FragmentActivity.launchAndRepeatWithLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

inline fun <T> Flow<T>.collectWithStateAtLeast(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.RESUMED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    collect {
        owner.withStateAtLeast(minActiveState) {  }
        action(it)
    }
}

fun Context.isRTL(): Boolean =
    TextUtilsCompat.getLayoutDirectionFromLocale(resources.configuration.locales[0]) == LayoutDirection.RTL