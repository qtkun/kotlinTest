package com.qtk.kotlintest.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlin.math.abs
import kotlin.math.sqrt

class ZoomImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
): AppCompatImageView(context, attributeSet, defStyleAttr) {
    companion object {
        private const val SCREEN_MAX_SCALE = 6f
        private const val SCREEN_MIN_SCALE = 0.4f
    }
    
    private var maxScale = 0f
    private var minScale = 0f
    private var mode: Mode = Mode.NONE
    private val matrix = Matrix()
    private val savedMatrix = Matrix()
    private val startPoint = PointF()
    private val midPoint = PointF()
    private var oldDistance = 1f
    private var lastClickTime: Long = 0
    private var lastClickPos = 0f
    private val matrixValues = FloatArray(9)

    private var drawableWidth = 0f
    private var drawableHeight = 0f

    init {
        scaleType = ScaleType.MATRIX
    }
    
    private val requestListener = object: RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            resource?.let {
                drawableWidth = it.intrinsicWidth.toFloat()
                drawableHeight = it.intrinsicHeight.toFloat()
                maxScale = width * SCREEN_MAX_SCALE / it.intrinsicWidth.toFloat()
                minScale = width * SCREEN_MIN_SCALE / it.intrinsicWidth.toFloat()
            }
            resetImage(resource)
            return false
        }

    }
    
    fun setUrl(url: String?) {
        Glide.with(context)
            .load(url)
            .addListener(requestListener)
            .into(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                doubleClicked(event)
                if (dragParentAllowIntercept(event)) {
                    savedMatrix.set(matrix)
                    startPoint.set(event.x, event.y)
                    mode = Mode.DRAG
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                oldDistance = getDistance(event)
                if (oldDistance > 10f) {
                    savedMatrix.set(matrix)
                    getMidPoint(midPoint, event)
                    mode = Mode.ZOOM
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (mode == Mode.DRAG) {
                    if (dragParentAllowIntercept(event)) {
                        matrix.set(savedMatrix)
                        matrix.postTranslate(event.x - startPoint.x, event.y - startPoint.y)
                    }
                } else if (mode == Mode.ZOOM) {
                    val newDistance = getDistance(event)
                    if (newDistance > 10f) {
                        parent.requestDisallowInterceptTouchEvent(true)
                        matrix.set(savedMatrix)
                        matrix.getValues(matrixValues)
                        var scale = newDistance / oldDistance
                        val newScale = scale * matrixValues[0]
                        if (minScale != 0f && maxScale != 0f) {
                            if (newScale < minScale) {
                                scale = minScale / matrixValues[0]
                            } else if (newScale > maxScale) {
                                scale = maxScale / matrixValues[0]
                            }
                        }
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y)
                    } else {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                imageMatrix = matrix
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                mode = Mode.NONE
            }
        }
        return true
    }

    private fun dragParentAllowIntercept(event: MotionEvent): Boolean {
        matrix.getValues(matrixValues)
        val scale = matrixValues[0]
        val realWidth = scale * drawableWidth
        val realHeight = scale * drawableHeight
        val pointStart = PointF(matrixValues[2], matrixValues[5])
        val pointEnd = PointF(pointStart.x + realWidth, pointStart.y + realHeight)
        //当图片放大且点击点在图片内时拦截父控件事件
        val interceptParent = (event.x in pointStart.x..pointEnd.x && event.y in pointStart.y..pointEnd.y)
                && (realWidth > width || realHeight > height)
        parent.requestDisallowInterceptTouchEvent(interceptParent)
        return interceptParent
    }

    private fun doubleClicked(event: MotionEvent) {
        val currentTime = System.currentTimeMillis()
        val currentPos = event.x
        if (currentTime - lastClickTime < 500 && abs(currentPos - lastClickPos) < 50) {
            // 双击事件
            resetImage(drawable)
        }
        lastClickTime = currentTime
        lastClickPos = currentPos
    }

    private fun resetImage(drawable: Drawable?) {
        drawable?.let {
            val imageRectF = RectF(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
            val viewRectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            matrix.setRectToRect(imageRectF, viewRectF, Matrix.ScaleToFit.CENTER)
            imageMatrix = matrix
        }
    }

    private fun getDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun getMidPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    enum class Mode{
        NONE,
        DRAG,
        ZOOM
    }
}