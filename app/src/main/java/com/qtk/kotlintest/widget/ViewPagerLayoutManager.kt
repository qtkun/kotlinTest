package com.qtk.kotlintest.widget

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import androidx.recyclerview.widget.RecyclerView.Recycler

/**
 * create on 2018/11/23
 * description  ViewPager页面切换类型LayoutManager，监听了item的进入和退出并回调
 */
class ViewPagerLayoutManager @JvmOverloads constructor(
    context: Context,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {
    companion object {
        private const val OFFSCREEN_PAGE_LIMIT_DEFAULT = -1
    }
    private var mPagerSnapHelper: PagerSnapHelper = PagerSnapHelper()
    private var mOnViewPagerListener: OnViewPagerListener? = null
    private var mRecyclerView: RecyclerView? = null

    var offscreenPageLimit = OFFSCREEN_PAGE_LIMIT_DEFAULT

    /**
     * 位移，用来判断移动方向
     */
    private var mDrift = 0

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        this.mRecyclerView = view
        try {
            //attachToRecyclerView源码上的方法可能会抛出IllegalStateException异常，这里手动捕获一下
            val onFlingListener = mRecyclerView!!.onFlingListener
            //源码中判断了，如果onFlingListener已经存在的话，再次设置就直接抛出异常，那么这里可以判断一下
            if (onFlingListener == null) {
                mPagerSnapHelper.attachToRecyclerView(mRecyclerView)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        mRecyclerView!!.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener)
    }

    override fun onDetachedFromWindow(view: RecyclerView?, recycler: Recycler?) {
        super.onDetachedFromWindow(view, recycler)
        mRecyclerView?.removeOnChildAttachStateChangeListener(mChildAttachStateChangeListener)
    }

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
    }

    override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
        if (offscreenPageLimit == OFFSCREEN_PAGE_LIMIT_DEFAULT) {
            super.calculateExtraLayoutSpace(state, extraLayoutSpace)
            return
        }
        val offscreenSpace: Int = getPageSize() * offscreenPageLimit
        extraLayoutSpace[0] = offscreenSpace
        extraLayoutSpace[1] = offscreenSpace
    }

    private fun getPageSize(): Int {
        mRecyclerView?.let { rv ->
            return if (orientation == RecyclerView.HORIZONTAL)
                rv.width - rv.paddingLeft - rv.paddingRight
            else rv.height - rv.paddingTop - rv.paddingBottom
        }
        return 0
    }

    /**
     * 滑动状态的改变
     *
     * @param state
     */
    override fun onScrollStateChanged(state: Int) {
        when (state) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                val viewIdle = mPagerSnapHelper.findSnapView(this) ?: return
                val positionIdle = getPosition(viewIdle)
                /*if (mOnViewPagerListener != null && childCount == 1) {
                    mOnViewPagerListener!!.onPageSelected(positionIdle, positionIdle == itemCount - 1)
                }*/
                mOnViewPagerListener?.onPageSelected(positionIdle, positionIdle == itemCount - 1)
                mOnViewPagerListener?.onPreloadResume(positionIdle, mDrift < 0)
            }
            RecyclerView.SCROLL_STATE_DRAGGING -> {
                val viewDrag = mPagerSnapHelper.findSnapView(this)
                if (viewDrag != null) {
                    val positionDrag = getPosition(viewDrag)
                    mOnViewPagerListener?.onPreloadPause(positionDrag, mDrift < 0)
                }
            }
            RecyclerView.SCROLL_STATE_SETTLING -> {
                val viewSettling = mPagerSnapHelper.findSnapView(this)
                if (viewSettling != null) {
                    val positionSettling = getPosition(viewSettling)
                    mOnViewPagerListener?.onPreloadPause(positionSettling, mDrift < 0)
                }
            }
        }
    }

    /**
     * 监听竖直方向的相对偏移量
     *
     * @param dy
     * @param recycler
     * @param state
     * @return
     */
    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        //需要判断子类的数量不能为0的情况
        if (childCount == 0 || dy == 0) {
            return 0
        }
        mDrift = dy
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    /**
     * 监听水平方向的相对偏移量
     *
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: RecyclerView.State): Int {
        //需要判断子类的数量不能为0的情况
        if (childCount == 0 || dx == 0) {
            return 0
        }
        mDrift = dx
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    fun setOnViewPagerListener(listener: OnViewPagerListener?) {
        mOnViewPagerListener = listener
    }

    private val mChildAttachStateChangeListener: OnChildAttachStateChangeListener = object : OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            if (childCount == 1) {
                mOnViewPagerListener?.onInitComplete()
            }
        }

        override fun onChildViewDetachedFromWindow(view: View) {
            if (mDrift >= 0) {
                mOnViewPagerListener?.onPageRelease(true, getPosition(view))
            } else {
                mOnViewPagerListener?.onPageRelease(false, getPosition(view))
            }
        }
    }
}

interface OnViewPagerListener {
    /**
     * 初始化完成
     */
    fun onInitComplete()

    /**
     * 释放的监听
     */
    fun onPageRelease(isNext: Boolean, position: Int)

    /**
     * 选中的监听以及判断是否滑动到底部
     */
    fun onPageSelected(position: Int, isBottom: Boolean)

    fun onPreloadResume(position: Int, isReverseScroll: Boolean)

    fun onPreloadPause(position: Int, isReverseScroll: Boolean)
}