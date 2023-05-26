package com.qtk.kotlintest.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.use
import androidx.core.view.isVisible
import com.qtk.kotlintest.R
import com.qtk.kotlintest.extensions.asDp
import kotlin.math.max
import kotlin.properties.Delegates

class FlowLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {
    companion object {
        private const val TAG = "FlowLayout"
    }

    //每个item横向间距
    private var mHorizontalSpacing by Delegates.notNull<Int>()

    //item的行间距
    private var mVerticalSpacing by Delegates.notNull<Int>()

    // 记录所有的行，一行一行的存储，用于layout
    private val allLines = mutableListOf<List<View>>()

    // 记录每一行的行高，用于layout
    private val lineHeights = arrayListOf<Int>()

    private fun clearMeasureParams() {
        allLines.clear()
        lineHeights.clear()
    }

    init {
        context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout).use {
            mHorizontalSpacing = it.getDimension(R.styleable.FlowLayout_horizontal_spacing, 10f.asDp()).toInt()
            mVerticalSpacing = it.getDimension(R.styleable.FlowLayout_vertical_spacing, 8f.asDp()).toInt()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        clearMeasureParams()
        val selfWidth = MeasureSpec.getSize(widthMeasureSpec)
        val selfHeight = MeasureSpec.getSize(heightMeasureSpec)
        var lineViews = mutableListOf<View>()
        //记录这行已经使用了多宽
        var lineWidthUsed = 0
        // 一行的行高
        var lineHeight = 0
        // measure过程中，子View要求的父ViewGroup的宽
        var parentNeededWidth = 0
        // measure过程中，子View要求的父ViewGroup的高
        var parentNeededHeight = 0

        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val childLP = childView.layoutParams
            if (childView.isVisible) {
                //getChildMeasureSpec在于结合我们从子视图的LayoutParams所给出的MeasureSpec信息来获取最合适的结果
                val childWidthMeasureSpec =
                    getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, childLP.width)
                val childHeightMeasureSpec =
                    getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, childLP.height)
                childView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
                //获取子view的度量宽高
                val childMeasuredWidth = childView.measuredWidth
                val childMeasuredHeight = childView.measuredHeight
                if (childMeasuredWidth + lineWidthUsed + mHorizontalSpacing > selfWidth) {
                    allLines.add(lineViews)
                    lineHeights.add(lineHeight)
                    //ViewGroup 的实际高度 = 每一行的高度+行间距
                    parentNeededHeight += lineHeight + mVerticalSpacing
                    //ViewGroup 的实际宽度 = 自view布局产生的最大宽度
                    parentNeededWidth = max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing)
                    //换行之后重置
                    lineViews = mutableListOf()
                    lineWidthUsed = 0
                    lineHeight = 0
                }
                // view 是分行layout的，所以要记录每一行有哪些view，这样可以方便layout布局
                lineViews.add(childView)
                //每行都会有自己的宽和高 每行已经用过的宽度 = 子view宽度+行间距
                lineWidthUsed += childMeasuredWidth + mHorizontalSpacing
                //行高
                lineHeight = max(lineHeight, childMeasuredHeight)
                //处理最后一行数据
                if (i == childCount - 1) {
                    allLines.add(lineViews)
                    lineHeights.add(lineHeight)
                    parentNeededHeight += lineHeight + mVerticalSpacing
                    parentNeededWidth = max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing)
                }
            }
        }

        //再度量自己的高度保存
        //根据子View的度量结果，来重新度量自己ViewGroup
        //作为一个ViewGroup，它自己也是一个View,它的大小也需要根据它的父View给它提供的宽高来度量
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val realWidth = if (widthMode == MeasureSpec.EXACTLY) selfWidth else parentNeededWidth
        val realHeight = if (heightMode == MeasureSpec.EXACTLY) selfHeight else parentNeededHeight
        setMeasuredDimension(realWidth, realHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //行数
        val lineCount = allLines.size
        //去掉父view的Padding的实际的左上起点
        var curL = paddingLeft
        var curT = paddingTop
        for (i in 0 until lineCount) {
            //获取每一行的view
            val lineViews = allLines[i]
            //获取每一行的高度
            val lineHeight = lineHeights[i]
            lineViews.forEach {
                //每个view 的上下左右点
                val left = curL
                val top = curT
                val right = left + it.measuredWidth
                val bottom = top + it.measuredHeight
                //获取view的上下左右去布局
                it.layout(left,top,right,bottom)
                //下一个view的左起点 上一个view的右起点+行间距
                curL = right + mHorizontalSpacing
            }
            //下一次的启动起点 = 行高 + 行间距
            curT += lineHeight + mVerticalSpacing
            //重置左起点
            curL = paddingLeft
        }
    }
}