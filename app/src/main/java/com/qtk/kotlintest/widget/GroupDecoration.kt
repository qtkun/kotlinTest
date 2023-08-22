package com.qtk.kotlintest.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.R
import com.qtk.kotlintest.extensions.asDp
import com.qtk.kotlintest.extensions.asSp
import com.qtk.kotlintest.extensions.dp

class GroupDecoration(
    private val backgroundColor: Int = Color.MAGENTA,
    private val textColor: Int = Color.WHITE,
    private val testSize: Float = 16f.asSp(),
    private val paddingLeft: Float = 20f.asDp(),
    private val height: Float = 50f.asDp(),
    private val tag: (Int) -> String?
) : RecyclerView.ItemDecoration() {
    private val paint by lazy {
        Paint().apply {
            color = backgroundColor
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    private val textPaint by lazy {
        TextPaint().apply {
            color = textColor
            textSize = testSize
            isAntiAlias = true
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        var preTag: String?
        var curTag: String? = null
        for(i in 0 until parent.childCount) {
            val child = parent[i]
            val position = parent.getChildAdapterPosition(child)
            preTag = curTag
            curTag = tag(position)
            if (curTag.isNullOrEmpty() || preTag == curTag) continue
            /*var tagBottom = max(height, child.top.toFloat())
            if (position + 1 < state.itemCount) {
                val nextTag = tag(position + 1)
                if (!TextUtils.equals(curTag, nextTag) && child.bottom.toFloat() < tagBottom) {
                    tagBottom = child.bottom.toFloat()
                }
            }*/
            c.drawRect(
                child.left.toFloat(),
                child.top - height,
                child.right.toFloat(),
                child.top.toFloat(),
                paint
            )
            c.drawText(curTag, child.left + paddingLeft, child.top - height / 2 + testSize / 2, textPaint)
            val imgSize = 16.dp
            val left = child.right - paddingLeft - imgSize
            val right = child.right - paddingLeft
            val top = child.top - height / 2 - imgSize / 2
            val bottom = child.top - height / 2 + imgSize / 2
            AppCompatResources.getDrawable(parent.context, R.drawable.ic_place_holder)
                ?.toBitmap(imgSize, imgSize)?.let {
                    c.drawBitmap(it, null, RectF(left, top, right, bottom), textPaint)
                    it.recycle()
                }
//            val textWidth = textPaint.measureText(curTag)
//            c.drawText(curTag, child.right - paddingLeft - textWidth, child.top - height / 2 + testSize / 2, textPaint)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (tag(position).isNullOrEmpty()) return
        if (position == 0|| isFirstInGroup(position)) {
            outRect.top = height.toInt()
        } else {
            outRect.top = 12.dp
        }
    }

    private fun isFirstInGroup(pos: Int): Boolean {
        return if (pos == 0) {
            true
        } else {
            val prevTag = tag(pos - 1)
            val curTag = tag(pos)
            prevTag != curTag
        }
    }
}