package com.qtk.kotlintest.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.extensions.asDp

class SpaceDecoration(
    private val space: Int = 8.asDp(),
    private val spanCount: Int = 2
): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        outRect.left = column * this.space / spanCount
        outRect.right = this.space - (column + 1) * this.space / spanCount
        if (position >= spanCount) {
            outRect.top = this.space
        }
    }
}