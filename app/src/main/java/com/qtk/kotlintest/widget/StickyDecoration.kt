package com.qtk.kotlintest.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.view.View
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.extensions.dpToPx
import com.qtk.kotlintest.extensions.spToPx
import kotlin.math.max

class StickyDecoration(
    private val backgroundColor: Int = Color.MAGENTA,
    private val textColor: Int = Color.WHITE,
    private val testSize: Float = 16f.spToPx(),
    private val paddingLeft: Float = 20f.dpToPx(),
    private val height: Float = 50f.dpToPx(),
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
        Paint().apply {
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
            if (curTag.isNullOrEmpty() or TextUtils.equals(preTag, curTag)) continue
            var tagBottom = max(height, child.top.toFloat())
            if (position + 1 < state.itemCount) {
                val nextTag = tag(position + 1)
                if (!TextUtils.equals(curTag, nextTag) && child.bottom.toFloat() < tagBottom) {
                    tagBottom = child.bottom.toFloat()
                }
            }
            c.drawRect(
                child.left.toFloat(),
                tagBottom - height,
                child.right.toFloat(),
                tagBottom,
                paint
            )
            c.drawText(curTag!!, child.left + paddingLeft, tagBottom - height / 2 + testSize / 2, textPaint)
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
        }
    }

    private fun isFirstInGroup(pos: Int): Boolean {
        return if (pos == 0) {
            true
        } else {
            val prevTag = tag(pos - 1)
            val curTag = tag(pos)
            !TextUtils.equals(prevTag, curTag)
        }
    }
}