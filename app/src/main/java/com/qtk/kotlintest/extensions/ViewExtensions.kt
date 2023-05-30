package com.qtk.kotlintest.extensions

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.util.TypedValue
import android.view.ActionMode
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

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
fun View.clickFlow() = callbackFlow {
    setOnClickListener { trySend(Unit) }
    awaitClose { setOnClickListener(null) }
}

fun <T> Flow<T>.throttleFirst(thresholdMillis: Long): Flow<T> = flow {
    var lastTime = 0L // 上次发射数据的时间
    // 收集数据
    collect { upstream ->
        // 当前时间
        val currentTime = System.currentTimeMillis()
        // 时间差超过阈值则发送数据并记录时间
        if (currentTime - lastTime > thresholdMillis) {
            lastTime = currentTime
            emit(upstream)
        }
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

//'为 RecyclerView 扩展表项点击监听器'
fun RecyclerView.setOnItemClickListener(listener: (View, Int) -> Unit) {
    //'为 RecyclerView 子控件设置触摸监听器'
    addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
        //'构造手势探测器，用于解析单击事件'
        val gestureDetector = GestureDetector(context, object : GestureDetector.OnGestureListener {
            override fun onShowPress(e: MotionEvent?) {
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                //'当单击事件发生时，寻找单击坐标下的子控件，并回调监听器'
                e?.let {
                    findChildViewUnder(it.x, it.y)?.let { child ->
                        listener(child, getChildAdapterPosition(child))
                    }
                }
                return false
            }

            override fun onDown(e: MotionEvent?): Boolean {
                return false
            }

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                return false
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                return false
            }

            override fun onLongPress(e: MotionEvent?) {
            }
        })

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        //'在拦截触摸事件时，解析触摸事件'
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            gestureDetector.onTouchEvent(e)
            return false
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        }
    })
}

fun View.rounded(radius: Float) {
    outlineProvider = object: ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline?) {
            outline?.setRoundRect(Rect(0, 0, view.width, view.height), radius)
        }
    }
    clipToOutline = true
}

fun View.circle() {
    outlineProvider = object: ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline?) {
            outline?.setOval(Rect(0, 0, view.width, view.height))
        }
    }
    clipToOutline = true
}

fun View.hideKeyboard() {
    context.getSystemService(InputMethodManager::class.java)?.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    context.getSystemService(InputMethodManager::class.java)?.showSoftInput(this, 0)
}

fun TextView.setSelectionMenu(textMenuItemOnClickListener: TextMenuItemOnClickListener) {
    customSelectionActionModeCallback = object: ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.let {
                for(i in 0 until menu.size()) {
                    val item = menu.getItem(i)
                    if (item.itemId == android.R.id.shareText) {
                        menu.removeItem(item.itemId)
                        break
                    }
                }
                menu.add(Menu.NONE, R.id.message_delete, 101, "删除")
            }
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when(item?.itemId) {
                R.id.message_delete -> {
                    textMenuItemOnClickListener.onMessageDelete()
                    mode?.finish()
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {}
    }
}

interface TextMenuItemOnClickListener {
    fun onMessageDelete()
}
