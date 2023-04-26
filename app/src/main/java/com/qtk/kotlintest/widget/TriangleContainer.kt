package com.qtk.kotlintest.widget

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.res.use
import com.qtk.kotlintest.R
import com.qtk.kotlintest.extensions.dpToPx
import com.qtk.kotlintest.extensions.getScreenHeight
import com.qtk.kotlintest.extensions.getScreenWidth
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

class TriangleContainer@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {
    companion object{
        const val TOP = 0
        const val BOTTOM = 1
        const val LEFT = 2
        const val RIGHT = 3
    }

    private var defaultWidth by Delegates.notNull<Int>()
    private var defaultHeight by Delegates.notNull<Int>()
    private val defaultShadowWidth = 8f.dpToPx()
    private val defaultPadding = 20f.dpToPx()
    private val defaultTrvWidth = 14f.dpToPx()
    private val defaultTrvHeight = 6f.dpToPx()
    private val defaultRadius = 4f.dpToPx()
    private var mColor by Delegates.notNull<Int>()
    private var mDirection by Delegates.notNull<Int>()
    private var mShadowWidth by Delegates.notNull<Float>()
    private var mPadding by Delegates.notNull<Float>()
    private var mTrvWidth by Delegates.notNull<Float>()
    private var mTrvHeight by Delegates.notNull<Float>()
    private var mRadius by Delegates.notNull<Float>()
    private val mPaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val mBgPaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.WHITE
    }
    private val mPath: Path = Path()

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        context.obtainStyledAttributes(attributeSet, R.styleable.TriangleContainer).use {
            mColor = it.getColor(R.styleable.TriangleContainer_trv_color, Color.DKGRAY)
            mDirection = it.getInt(R.styleable.TriangleContainer_trv_direction, TOP)
            mShadowWidth = it.getDimension(R.styleable.TriangleContainer_trv_shadow_width, defaultShadowWidth)
            mPadding = it.getDimension(R.styleable.TriangleContainer_trv_padding, defaultPadding)
            mTrvWidth = it.getDimension(R.styleable.TriangleContainer_trv_width, defaultTrvWidth)
            mTrvHeight = it.getDimension(R.styleable.TriangleContainer_trv_height, defaultTrvHeight)
            mRadius = it.getDimension(R.styleable.TriangleContainer_trv_radius, defaultRadius)
        }
        mPaint.color = mColor
        if (mShadowWidth > 0f) {
            mPaint.maskFilter = BlurMaskFilter(mShadowWidth, BlurMaskFilter.Blur.OUTER)
        }
        initWH()
    }

    private fun initWH() {
        when (mDirection) {
            TOP, BOTTOM -> {
                defaultWidth = ((mShadowWidth) * 2 + padding + mTrvWidth).toInt()
                defaultHeight = (mShadowWidth * 2 + mTrvHeight).toInt()
            }
            LEFT, RIGHT -> {
                defaultWidth = (mShadowWidth * 2 + mTrvHeight).toInt()
                defaultHeight = ((mShadowWidth) * 2 + padding + mTrvWidth).toInt()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSpec = widthMeasureSpec
        var heightSpec = heightMeasureSpec
        if (childCount > 1) {
            throw IllegalStateException("子View只能有一个")
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        val child: View? = getChildAt(0)
        child?.let {
            val layoutParams = it.layoutParams
            if (layoutParams.width == LayoutParams.MATCH_PARENT) {
                widthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY)
            } else {
                when (mDirection) {
                    TOP, BOTTOM -> {
                        defaultWidth = max(defaultWidth, (child.measuredWidth + mShadowWidth * 2).toInt())
                    }
                    LEFT, RIGHT -> {
                        defaultWidth = max(defaultWidth, (child.measuredWidth + mShadowWidth * 2 + mTrvHeight).toInt())
                    }
                }
            }
            if (layoutParams.height == LayoutParams.MATCH_PARENT) {
                heightSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY)
            } else {
                when (mDirection) {
                    TOP, BOTTOM -> {
                        defaultHeight = max(defaultHeight, (child.measuredHeight + mShadowWidth * 2 + mTrvHeight).toInt())
                    }
                    LEFT, RIGHT -> {
                        defaultHeight = max(defaultHeight, (child.measuredHeight + mShadowWidth * 2).toInt())
                    }
                }
            }
        }
        setMeasuredDimension(getSize(widthSpec, defaultWidth), getSize(heightSpec, defaultHeight))
    }

    private fun getSize(measureSpec: Int, default: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.AT_MOST -> {
                min(default, specSize)
            }
            MeasureSpec.EXACTLY -> {
                specSize
            }
            MeasureSpec.UNSPECIFIED -> {
                default
            }
            else -> 0
        }
    }

    var padding: Float
        get() = mPadding
        set(value) {
            mPadding = value
        }

    var direction: Int
        get() = mDirection
        set(value) {
            mDirection = value
            initWH()
            requestLayout()
            invalidate()
        }

    var trvHeight: Float
        get() = mTrvHeight
        set(value) {
            mTrvHeight = value
            initWH()
            requestLayout()
            invalidate()
        }

    var trvWidth: Float
        get() = mTrvWidth
        set(value) {
            mTrvWidth = value
            initWH()
            requestLayout()
            invalidate()
        }

    var shadowWidth: Float
        get() = mShadowWidth
        set(value) {
            mShadowWidth = value
            if (mShadowWidth > 0f) {
                mPaint.maskFilter = BlurMaskFilter(mShadowWidth, BlurMaskFilter.Blur.OUTER)
            }
            initWH()
            requestLayout()
            invalidate()
        }

    var radius: Float
        get() = mRadius
        set(value) {
            mRadius = value
            initWH()
            requestLayout()
            invalidate()
        }

    override fun dispatchDraw(canvas: Canvas?) {
        mPath.reset()
        when(mDirection) {
            TOP -> {
                mPath.moveTo(width - mShadowWidth - mPadding, mShadowWidth + mTrvHeight)
                mPath.lineTo(width - mShadowWidth - (mPadding + mTrvWidth / 2), mShadowWidth)
                mPath.lineTo(width - mShadowWidth - (mPadding + mTrvWidth), mShadowWidth + mTrvHeight)
                mPath.addRoundRect(mShadowWidth, mShadowWidth + mTrvHeight,
                    width - mShadowWidth, height - mShadowWidth, mRadius, mRadius, Path.Direction.CW)
            }
            BOTTOM -> {
                mPath.moveTo(mShadowWidth + mPadding, height - mShadowWidth - mTrvHeight)
                mPath.lineTo(mShadowWidth + mPadding + mTrvWidth / 2, height - mShadowWidth)
                mPath.lineTo(mShadowWidth + mPadding + mTrvWidth, height - mShadowWidth - mTrvHeight)
                mPath.addRoundRect(mShadowWidth, mShadowWidth,
                    width - mShadowWidth, height - mShadowWidth - mTrvHeight, mRadius, mRadius, Path.Direction.CW)
            }
            LEFT -> {
                mPath.moveTo(mShadowWidth + mTrvHeight, mShadowWidth + mPadding)
                mPath.lineTo(mShadowWidth, mShadowWidth + mPadding + mTrvWidth / 2)
                mPath.lineTo(mShadowWidth + mTrvHeight, mShadowWidth + mPadding + mTrvWidth)
                mPath.addRoundRect(mShadowWidth + mTrvHeight, mShadowWidth,
                    width - mShadowWidth, height - mShadowWidth, mRadius, mRadius, Path.Direction.CW)
            }
            RIGHT -> {
                mPath.moveTo(width - mShadowWidth - mTrvHeight, height - mShadowWidth - mPadding)
                mPath.lineTo(width - mShadowWidth, height - mShadowWidth - (mPadding + mTrvWidth / 2))
                mPath.lineTo(width - mShadowWidth - mTrvHeight, height - mShadowWidth - (mPadding + mTrvWidth))
                mPath.addRoundRect(mShadowWidth, mShadowWidth,
                    width - mShadowWidth - mTrvHeight, height - mShadowWidth, mRadius, mRadius, Path.Direction.CW)
            }
        }
        canvas?.drawPath(mPath, mBgPaint)
        canvas?.drawPath(mPath, mPaint)
        super.dispatchDraw(canvas)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        getChildAt(0)?.let {
            when (mDirection) {
                TOP -> {
                    it.layout(mShadowWidth.toInt(), (mShadowWidth + mTrvHeight).toInt(),
                        (width - mShadowWidth).toInt(), (height - mShadowWidth).toInt())
                }
                BOTTOM -> {
                    it.layout(mShadowWidth.toInt(), mShadowWidth.toInt(),
                        (width - mShadowWidth).toInt(), (height - mShadowWidth - mTrvHeight).toInt())
                }
                LEFT -> {
                    it.layout((mShadowWidth + mTrvHeight).toInt(), mShadowWidth.toInt(),
                        (width - mShadowWidth).toInt(), (height - mShadowWidth).toInt())
                }
                RIGHT -> {
                    it.layout(mShadowWidth.toInt(), mShadowWidth.toInt(),
                        (width - mShadowWidth - mTrvHeight).toInt(), (height - mShadowWidth).toInt())
                }
            }
        }
    }

    fun topDx(width: Int, anchor: View): Int {
        return (mTrvWidth / 2 + mPadding + anchor.width / 2 - width + mShadowWidth).toInt()
    }

    fun bottomDx(anchor: View): Int {
        return (anchor.width / 2 - mPadding - mShadowWidth - mTrvWidth / 2).toInt()
    }

    fun leftDy(anchor: View): Int {
        return (-anchor.height / 2 - mShadowWidth - mPadding - mTrvWidth / 2).toInt()
    }

    fun rightDy(height: Int, anchor: View): Int {
        return (mPadding + mShadowWidth + mTrvWidth / 2 - anchor.height / 2 - height).toInt()
    }
}

fun TriangleContainer.showTop(popupWindow: PopupWindow, anchor: View) {
    if (direction != TriangleContainer.BOTTOM) {
        direction = TriangleContainer.BOTTOM
    }
    popupWindow.contentView.measure(
        makePopupWindowMeasureSpec(popupWindow.width),
        makePopupWindowMeasureSpec(popupWindow.height)
    )
    val location = IntArray(2)
    anchor.getLocationOnScreen(location)
    val dx = bottomDx(anchor)
    val offset = (anchor.context as Activity).getScreenWidth() - location[0] + abs(dx) - popupWindow.contentView.measuredWidth
    if (offset < 0) {
        padding += abs(offset).toFloat()
    }
    if (popupWindow.contentView.measuredHeight > location[1]) {
        showBottom(popupWindow, anchor)
    } else {
        popupWindow.showAsDropDown(anchor, dx,
            -popupWindow.contentView.measuredHeight - anchor.height
        )
    }
}

fun TriangleContainer.showBottom(popupWindow: PopupWindow, anchor: View) {
    if (direction != TriangleContainer.TOP) {
        direction = TriangleContainer.TOP
    }
    popupWindow.contentView.measure(
        makePopupWindowMeasureSpec(popupWindow.width),
        makePopupWindowMeasureSpec(popupWindow.height)
    )
    val location = IntArray(2)
    anchor.getLocationOnScreen(location)
    val dx = topDx(popupWindow.contentView.measuredWidth, anchor)
    val offset = location[0] - abs(dx)
    if (offset < 0) {
        padding += abs(offset)
    }
    if ((popupWindow.contentView.measuredHeight + location[1] + anchor.measuredHeight) >
        (anchor.context as Activity).getScreenHeight()) {
        showTop(popupWindow, anchor)
    } else {
        popupWindow.showAsDropDown(anchor, dx, 0)
    }
}

fun TriangleContainer.showLeft(popupWindow: PopupWindow, anchor: View) {
    if (direction != TriangleContainer.RIGHT) {
        direction = TriangleContainer.RIGHT
    }
    popupWindow.contentView.measure(
        makePopupWindowMeasureSpec(popupWindow.width),
        makePopupWindowMeasureSpec(popupWindow.height)
    )
    val location = IntArray(2)
    anchor.getLocationOnScreen(location)
    if (popupWindow.contentView.measuredWidth > location[0]) {
        showRight(popupWindow, anchor)
    } else {
        popupWindow.showAsDropDown(
            anchor, -popupWindow.contentView.measuredWidth,
            rightDy(popupWindow.contentView.measuredHeight, anchor)
        )
    }
}

fun TriangleContainer.showRight(popupWindow: PopupWindow, anchor: View) {
    if (direction != TriangleContainer.LEFT) {
        direction = TriangleContainer.LEFT
    }
    popupWindow.contentView.measure(
        makePopupWindowMeasureSpec(popupWindow.width),
        makePopupWindowMeasureSpec(popupWindow.height)
    )
    val location = IntArray(2)
    anchor.getLocationOnScreen(location)
    if ((popupWindow.contentView.measuredWidth + location[0] + anchor.measuredWidth) >
        (anchor.context as Activity).getScreenWidth()) {
        showLeft(popupWindow, anchor)
    } else {
        popupWindow.showAsDropDown(anchor, anchor.width, leftDy(anchor))
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