package com.qtk.kotlintest.widget.nest

import android.content.Context
import android.util.AttributeSet
import android.widget.OverScroller
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Field

/**
 * 一个可以方便获取Y轴速率的RecyclerView基类
 */
open class BaseRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val overScroller: OverScroller by lazy {
        RecyclerView::class.java.getDeclaredField("mViewFlinger").run {
            isAccessible = true
            get(this@BaseRecyclerView)
        }.let {
            it.javaClass.getDeclaredField("mOverScroller").run {
                isAccessible = true
                get(it) as OverScroller
            }
        }
    }

    private val scrollerYObj: Any by lazy {
        // 3. scrollerY对象获取
        OverScroller::class.java.getDeclaredField("mScrollerY").run {
            isAccessible = true
            get(overScroller)
        }
    }

    private val velocityYField: Field by lazy {
        // 4. Y轴速率filed获取
        scrollerYObj.javaClass.getDeclaredField("mCurrVelocity").apply {
            isAccessible = true
        }
    }

    /**
     * 获取垂直方向的速率
     */
    fun getVelocityY(): Int = (velocityYField.get(scrollerYObj) as Float).toInt()
    /**
     * 停止滑动fling
     */
    fun stopFling() {
        this.overScroller.forceFinished(true)
        velocityYField.set(scrollerYObj, 0)
    }
}