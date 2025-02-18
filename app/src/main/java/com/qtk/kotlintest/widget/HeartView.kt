package com.qtk.kotlintest.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.qtk.kotlintest.extensions.asDp
import kotlin.math.min

class HeartView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attributeSet, defStyleAttr) {
    companion object {
        var DEFAULT_SIZE = 30.asDp().toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(getSize(widthMeasureSpec), getSize(heightMeasureSpec))
    }

    private fun getSize(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.AT_MOST -> {
                min(DEFAULT_SIZE, specSize)
            }
            MeasureSpec.EXACTLY -> {
                specSize
            }
            MeasureSpec.UNSPECIFIED -> {
                DEFAULT_SIZE
            }
            else -> 0
        }
    }

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 20f
        textSize = 40f
    }
    private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeWidth = 10f
        shader = this@HeartView.getShader()
    }
    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paintLine.shader = getShader()
        if (measuredWidth >= measuredHeight) {
            path.arcTo(
                measuredWidth / 2f - measuredHeight / 2f, 0f,
                measuredWidth / 2f, measuredHeight / 2f,
                180f, 180f, true
            )
            path.moveTo(measuredWidth / 2f - measuredHeight / 2f, measuredHeight / 4f)
            path.quadTo(
                measuredWidth / 2f - measuredHeight / 2f,
                measuredHeight / 2f,
                measuredWidth / 2f, measuredHeight.toFloat(),
            )
            path.quadTo(
                measuredWidth / 2f + measuredHeight / 2f,
                measuredHeight / 2f,
                measuredWidth / 2f + measuredHeight / 2f, measuredHeight / 4f,
            )
            path.arcTo(
                measuredWidth / 2f, 0f,
                measuredWidth / 2f + measuredHeight / 2f,
                measuredHeight / 2f, 180f, 180f, true
            )
            path.close()
            canvas.drawPath(path, paintLine)
        } else {
            path.arcTo(
                0f, measuredHeight / 2f - measuredWidth / 2f,
                measuredWidth / 2f, measuredHeight / 2f,
                180f, 180f, true
            )
            path.moveTo(0f, measuredHeight / 2f - measuredWidth / 4f)
            path.quadTo(
                0f, measuredHeight / 2f,
                measuredWidth / 2f,
                measuredHeight / 2f + measuredWidth / 2f
            )
            path.quadTo(
                measuredWidth.toFloat(), measuredHeight / 2f,
                measuredWidth.toFloat(),
                measuredHeight / 2f - measuredWidth / 4f
            )
            path.arcTo(
                measuredWidth / 2f,
                measuredHeight / 2f - measuredWidth / 2f, measuredWidth.toFloat(),
                measuredHeight / 2f, 180f, 180f, true
            )
            path.close()
            canvas.drawPath(path, paintLine)
        }
    }

    private fun getShader(): Shader {
        return LinearGradient(
            0f,
            0f,
            0f,
            measuredHeight.toFloat(),
            Color.argb(255, 250, 49, 33),
            Color.argb(165, 234, 115, 9),
            Shader.TileMode.CLAMP
        )
    }
}