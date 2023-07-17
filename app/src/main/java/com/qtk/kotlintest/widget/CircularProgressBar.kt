package com.qtk.kotlintest.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import com.qtk.kotlintest.R
import com.qtk.kotlintest.extensions.color
import com.qtk.kotlintest.extensions.dp
import com.qtk.kotlintest.extensions.sp
import kotlin.math.min
import kotlin.properties.Delegates

class CircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mRadius by Delegates.notNull<Int>()

    private var mStrokeWidth by Delegates.notNull<Int>()

    private var mShadowRadius by Delegates.notNull<Int>()

    private var mShadowColor by Delegates.notNull<Int>()

    private var mProgressBarBgColor by Delegates.notNull<Int>()

    private var mProgressColor by Delegates.notNull<Int>()

    private var mMaxProgress by Delegates.notNull<Float>()

    private var mCurrentProgress by Delegates.notNull<Float>()

    private var mShowText by Delegates.notNull<Boolean>()

    private var mTextSize by Delegates.notNull<Int>()

    private var mTextColor by Delegates.notNull<Int>()

    private var mText = ""

    private var mStartAngle = -90f

    private var mEndAngle = 360f

    private var mSwipeAngle = 0f

    private var mDuration = 200L

    private val mPaint: Paint

    private val mTextPaint: Paint

    private val mRectF = RectF()
    private val mTextBounds = Rect()

    val progress get() = mCurrentProgress

    private var valueAnimator: ValueAnimator? = null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar).use { ta ->
            mRadius = ta.getDimensionPixelSize(R.styleable.CircularProgressBar_radius, 60.dp)
            mStrokeWidth = ta.getDimensionPixelSize(R.styleable.CircularProgressBar_strokeWidth, 10.dp)
            mShadowRadius = ta.getDimensionPixelSize(R.styleable.CircularProgressBar_shadowRadius, 5.dp)
            mShadowColor = ta.getColor(
                R.styleable.CircularProgressBar_shadowColor, Color.parseColor("#4D000000"))
            mProgressBarBgColor = ta.getColor(
                R.styleable.CircularProgressBar_progressbarBackgroundColor, Color.parseColor("#e5e5e5"))
            mProgressColor = ta.getColor(
                R.styleable.CircularProgressBar_progressbarColor, Color.parseColor("#4E6DFB"))
            mMaxProgress = ta.getInt(R.styleable.CircularProgressBar_maxProgress, 100).toFloat()
            mCurrentProgress = ta.getInteger(R.styleable.CircularProgressBar_progress, 0).toFloat()
            mShowText = ta.getBoolean(R.styleable.CircularProgressBar_showText, true)
            mTextSize = ta.getDimensionPixelSize(R.styleable.CircularProgressBar_textSize, 14.sp)
            mTextColor = ta.getColor(
                R.styleable.CircularProgressBar_textColor,
                context.color(R.color.text_main)
            )
        }
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mStrokeWidth.toFloat()
        }
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = mTextSize.toFloat()
            color = mTextColor
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val specMode = MeasureSpec.getMode(widthMeasureSpec)
        val specSize = MeasureSpec.getSize(widthMeasureSpec)
        val width = when (specMode) {
            MeasureSpec.AT_MOST -> {
                min((mRadius + mShadowRadius + mStrokeWidth) * 2, specSize)
            }
            MeasureSpec.EXACTLY -> {
                specSize
            }
            MeasureSpec.UNSPECIFIED -> {
                (mRadius + mShadowRadius + mStrokeWidth)
            }
            else -> 0
        }
        if (mRadius > (width - (mStrokeWidth + mShadowRadius) * 2) / 2) {
            mRadius = (width - (mStrokeWidth + mShadowRadius) * 2) / 2
        }
        setMeasuredDimension(width, width)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val centerX = (width / 2).toFloat()
        mRectF.apply {
            left = (mStrokeWidth + mShadowRadius).toFloat()
            right = (width - mStrokeWidth - mShadowRadius).toFloat()
            top = (mStrokeWidth + mShadowRadius).toFloat()
            bottom = (height - mStrokeWidth - mShadowRadius).toFloat()
        }
        mPaint.color = mProgressBarBgColor
        mPaint.strokeWidth = mStrokeWidth.toFloat()
        canvas?.drawCircle(centerX, centerX, mRadius.toFloat(), mPaint)
        mPaint.color = mProgressColor
        mPaint.strokeWidth = mStrokeWidth * 3f
        mPaint.setShadowLayer(mShadowRadius.toFloat(), 0f, 0f, mShadowColor)
        mSwipeAngle = mEndAngle * (mCurrentProgress / mMaxProgress)
        canvas?.drawArc(mRectF, mStartAngle, mSwipeAngle, false, mPaint)
        if (mShowText) {
            mText = "${(mCurrentProgress / mMaxProgress * mMaxProgress).toInt()}%"
            mTextPaint.getTextBounds(mText, 0, mText.length, mTextBounds)
            canvas?.drawText(
                mText,
                centerX - mTextBounds.width() / 2f,
                mTextBounds.height() / 2f + height / 2f,
                mTextPaint
            )
        }
        mPaint.clearShadowLayer()
    }

    fun setMaxProgress(maxProgress: Float) {
        mMaxProgress = maxProgress
        invalidate()
    }

    fun setProgress(progress: Float, isAnimation: Boolean = true) {
        valueAnimator?.cancel()
        if (isAnimation) {
            val progressVar = if (progress > mMaxProgress) mMaxProgress else progress
            val currentProgress = mCurrentProgress
            valueAnimator = ValueAnimator.ofFloat(currentProgress, progressVar).apply {
                duration = mDuration
                addUpdateListener {
                    mCurrentProgress = it.animatedValue as Float
                    invalidate()
                }
            }
            valueAnimator?.start()
        } else {
            mCurrentProgress = progress
            invalidate()
        }
    }

    fun setShadowRadius(shadowRadius: Int) {
        mShadowRadius = shadowRadius
        invalidate()
    }

    fun setShadowColor(@ColorInt shadowColor: Int) {
        mShadowColor = shadowColor
        invalidate()
    }

    fun setTextSize(textSize: Int) {
        mTextSize = textSize.sp
        invalidate()
    }

    fun setTextColor(@ColorInt textColor: Int) {
        mTextColor = textColor
        invalidate()
    }

    fun setProgressColor(@ColorInt progressColor: Int) {
        mProgressColor = progressColor
        invalidate()
    }

    fun setProgressBackGroundColor(@ColorInt progressBackGroundColor: Int) {
        mProgressBarBgColor = progressBackGroundColor
        invalidate()
    }

    fun setStrokeWidth(strokeWidth: Int) {
        mStrokeWidth = strokeWidth.dp
        invalidate()
    }

    fun setShowText(showText: Boolean) {
        mShowText = showText
        invalidate()
    }
}