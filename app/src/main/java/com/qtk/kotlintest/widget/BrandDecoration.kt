package com.qtk.kotlintest.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.extensions.dpToPx

class BrandDecoration(
    private val radius: Float = 8f.dpToPx()
): RecyclerView.ItemDecoration() {
    private val paint by lazy {
        Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            strokeJoin = Paint.Join.ROUND
            style = Paint.Style.FILL
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        for (child in parent.children) {
            if (child.isSelected) {
                val path = Path()
                val left = child.left.toFloat()
                val right = child.right.toFloat()
                val top = child.top.toFloat()
                val bottom = child.bottom.toFloat()
                val position = parent.getChildAdapterPosition(child)
                if (position == 0) {
                    path.apply {
                        moveTo(left, top)
                        lineTo(right - radius, top)
                        arcTo(right - 2 * radius, top, right, top + 2 * radius, -90f, 90f, false)
                        lineTo(right, bottom + radius)
                        arcTo(right - 2 * radius, bottom, right, bottom + 2 * radius, 0f, -90f, false)
                        lineTo(left, bottom)
                        close()
                    }
                } else {
                    path.apply {
                        moveTo(left, top)
                        lineTo(right - radius, top)
                        arcTo(right - 2 * radius, top - 2 * radius, right, top, 90f, -90f, false)
                        lineTo(right, bottom + radius)
                        arcTo(right - 2 * radius, bottom, right, bottom + 2 * radius, 0f, -90f, false)
                        lineTo(left, bottom)
                        close()
                    }
                }
                c.drawPath(path, paint)
                break
            }
        }
    }
}