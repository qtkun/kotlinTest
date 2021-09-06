package com.qtk.kotlintest.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.view.get
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.extensions.dpToPx

class TimeLineDecoration(
    private val color: Int = Color.MAGENTA,
    private val circleRadius: Float = (3.5f).dpToPx(),
    private val leftPadding: Float = 4f.dpToPx(),
    private val topPadding: Float = (3.5f).dpToPx()
): RecyclerView.ItemDecoration() {
    private val paint by lazy {
        Paint().apply {
            color = this@TimeLineDecoration.color
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeWidth = 1f.dpToPx()
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        for(i in 0 until parent.childCount) {
            val child = parent[i]
            val position = parent.getChildAdapterPosition(child)
            val cx = child.left - leftPadding + circleRadius
            val cy = child.top + circleRadius + topPadding
            c.drawCircle(cx, cy, circleRadius, paint)
            if (position < state.itemCount - 1) {
                val sy = cy + circleRadius
                val ey = child.bottom + child.marginBottom + topPadding
                c.drawLine(cx, sy, cx, ey, paint)
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = leftPadding.toInt()
    }
}