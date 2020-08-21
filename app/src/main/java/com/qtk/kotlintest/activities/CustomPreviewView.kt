package com.qtk.kotlintest.activities

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.camera.view.PreviewView

class CustomPreviewView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PreviewView(context, attrs, defStyleAttr) {
    private var mGestureDetector : GestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent?) {
                super.onLongPress(e)
                e?.x?.let {
                    mCustomTouchListener?.longClick(e.x, e.y)
                }
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                currentDistance = 0f
                lastDistance = 0f
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                e?.x?.let {
                    mCustomTouchListener?.click(e.x, e.y)
                }
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                e?.x?.let {
                    mCustomTouchListener?.doubleClick(e.x, e.y)
                }
                return true
            }
        })
    private val mScaleGestureDetector: ScaleGestureDetector =
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            /**
             * 缩放监听
             */
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                detector?.scaleFactor?.let {
                    mCustomTouchListener?.zoom(it)
                }
                return true
            }

        })
    private var mCustomTouchListener: CustomTouchListener? = null
    private var currentDistance = 0f
    private var lastDistance = 0f

    fun setCustomTouchListener(customTouchListener: CustomTouchListener) {
        mCustomTouchListener = customTouchListener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleGestureDetector.onTouchEvent(event);
        if (!mScaleGestureDetector.isInProgress) {
            mGestureDetector.onTouchEvent(event);
        }
        return true;
    }

}

/**
 * 缩放监听
 */
interface CustomTouchListener {
    /**
     * 放大
     */
    fun zoom(delta: Float)

    /**
     * 点击
     */
    fun click(x: Float, y: Float)

    /**
     * 双击
     */
    fun doubleClick(x: Float, y: Float)

    /**
     * 长按
     */
    fun longClick(x: Float, y: Float)
}