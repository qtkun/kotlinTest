package com.qtk.kotlintest.widget

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.databinding.LayoutStickFooterBinding
import com.qtk.kotlintest.extensions.dp
import com.qtk.kotlintest.extensions.mergeViewBinding
import kotlin.math.min

class HorStickyNavLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent2 {
    companion object {
        private const val TAG = "HorStickyNavLayout"
        private val MAX_SCROLL_X = 100.dp
        private val MAX_FLING_SCROLL_X = 1.5f * MAX_SCROLL_X
        val FACTOR = 60.dp
        val START_FACTOR = 40.dp
    }
    private var mChildView: RecyclerView? = null

    private var springAnimator: ValueAnimator? = null

    private var mFooterView = StickyFooterView(context).apply {
        layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.END
        }
    }

    private var hasJump = false

    private var startActivity: StartActivity? = null

    init {
        orientation = HORIZONTAL
    }

    fun setStartActivity(startActivity: StartActivity) {
        this.startActivity = startActivity
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            mChildView = getChildAt(0) as? RecyclerView
            addView(mFooterView)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = mChildView?.measuredHeight ?: 0
        mFooterView.layoutParams.height = height
        mFooterView.requestLayout()
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        hasJump = false
        return target is RecyclerView
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {

    }

    override fun onStopNestedScroll(target: View, type: Int) {
        if (type == ViewCompat.TYPE_TOUCH && scrollX > FACTOR) {
            startActivity?.invoke()
            hasJump = true
        } else if (type == ViewCompat.TYPE_NON_TOUCH && scrollX > FACTOR) {
            if (!hasJump) {
                startActivity?.invoke()
            }
        }
        if (scrollX > 0) {
            if (springAnimator != null) return
            springAnimator = ValueAnimator.ofInt(scrollX, 25.dp).apply {
                addUpdateListener {
                    (it.animatedValue as Int).let { value ->
                        scrollTo(value, 0)
                        mFooterView.run {
                            layoutParams.width = value
                            requestLayout()
                            show(value)
                        }
                    }
                }
                doOnEnd {
                    springAnimator = null
                }
            }.also { it.start() }
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {

    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val targetCanHorizontally = target.canScrollHorizontally(1)
        if (dx > 0 && !targetCanHorizontally) {
            if (type == ViewCompat.TYPE_NON_TOUCH) {
                springAnimator?.cancel()
                /*if (scrollX < MAX_FLING_SCROLL_X) {
                    val factor = 1f - min(1f, scrollX / MAX_FLING_SCROLL_X)
                    var scrollByX = (dx * factor).toInt()
                    if ((scrollByX + scrollX) > MAX_FLING_SCROLL_X) {
                        scrollByX = MAX_FLING_SCROLL_X.toInt() - scrollX
                    }
                    scrollBy(scrollByX, 0)
                    consumed[0] = dx
                    mFooterView.show(scrollX)
                }*/
            }
            if (scrollX < MAX_SCROLL_X) {
                val factor = 1f - min(1f, scrollX.toFloat() / MAX_SCROLL_X)
                var scrollByX = (dx * factor).toInt()
                Log.e(TAG, "scrollByX = $scrollByX")
                if ((scrollByX + scrollX) > MAX_SCROLL_X) {
                    scrollByX = MAX_SCROLL_X - scrollX
                }
                scrollBy(scrollByX, 0)
                consumed[0] = dx
                mFooterView.show(scrollX)
            }
        } else if (dx < 0 && !targetCanHorizontally) {
            if (scrollX > 0) {
                var scrollByX = dx
                if ((scrollX + dx) < 0) {
                    scrollByX = 0 - scrollX
                }
                scrollBy(scrollByX, 0)
                consumed[0] = scrollByX
                mFooterView.show(scrollX)
            }
        }
    }

    fun interface StartActivity: () -> Unit
}

class StickyFooterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): RelativeLayout(context, attrs, defStyleAttr) {
    private val binding by mergeViewBinding<LayoutStickFooterBinding>()

    fun show(scrollX: Int) {
        layoutParams.width = scrollX
        requestLayout()
        with(binding) {
            tvTips.text = if (scrollX >= HorStickyNavLayout.FACTOR) "全部" else "更多"
            if (scrollX > HorStickyNavLayout.START_FACTOR) {
                val rotation = min(1f, (scrollX - HorStickyNavLayout.START_FACTOR).toFloat() /
                        (HorStickyNavLayout.FACTOR - HorStickyNavLayout.START_FACTOR)) * 180f
                ivArrow.rotation = -rotation
            }
        }
    }
}
