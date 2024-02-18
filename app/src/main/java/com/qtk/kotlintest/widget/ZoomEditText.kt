package com.qtk.kotlintest.widget

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionLayout.TransitionListener
import androidx.core.content.res.use
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.util.forEach
import androidx.core.widget.doOnTextChanged
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ScreenUtils
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.ZoomEditTextLayoutBinding
import com.qtk.kotlintest.extensions.color
import com.qtk.kotlintest.extensions.mergeViewBinding


class ZoomEditText @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attributeSet, defStyleAttr) {
    companion object {
        private const val TAG_PREFIX = '#'
    }
    private val binding by mergeViewBinding<ZoomEditTextLayoutBinding>()

    private var startHeight = 0
    private var endHeight = 0
    private var startWidth = 0
    private var endWidth = 0
    private var isZoomOut = false

    private var isFirst = true
    private var parentView: ViewGroup? = null
    private var firstLayoutParams: ViewGroup.LayoutParams? = null
    private var indexOfChild: Int = 0
    private var decorView: FrameLayout? = null
    private var topMargin = 0
    private var leftMargin = 0

    private var emptyView: View? = null

    private var tags: SparseArray<TagBean> = SparseArray()
    private var onTagDeleteListener: ((Int) -> Unit)? = null
    var content = ""
        private set

    init {
        (context as? Activity)?.let {
            decorView = it.findViewById(android.R.id.content)
        }

        var hint: String? = null
        context.obtainStyledAttributes(attributeSet, R.styleable.ZoomEditText).use {
            hint = it.getString(R.styleable.ZoomEditText_text_hint)
        }

        with(binding) {
            editText.hint = hint
            editText.doOnTextChanged { text, start, before, count ->
                doOnTextChanged(before, count, text, start)
            }
            editText.setOnKeyListener { _, keyCode, event ->
                doOnDelClick(keyCode, event)
                false
            }
            motionRoot.setTransitionListener(object: TransitionListener {
                override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
                    KeyboardUtils.hideSoftInput(editText)
                    transitionStarted()
                }

                override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}

                override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                    postDelayed({ KeyboardUtils.showSoftInput(editText) }, 50)
                    transitionCompleted()
                }

                override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
            })
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (parentView == null) {
            parentView = (parent as? ViewGroup)?.also {
                indexOfChild = it.indexOfChild(this)
            }
            firstLayoutParams = layoutParams
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isFirst) {
            emptyView = View(context).apply {
                layoutParams = ViewGroup.LayoutParams(this@ZoomEditText.width, this@ZoomEditText.height)
            }
            startHeight = height
            endHeight = ScreenUtils.getScreenHeight() - BarUtils.getStatusBarHeight()
            startWidth = width
            endWidth = ScreenUtils.getScreenWidth()
            val location = IntArray(2).also { getLocationOnScreen(it) }
            topMargin = location[1] - BarUtils.getStatusBarHeight()
            leftMargin = location[0]
            isFirst = false
        }
    }

    private fun doOnDelClick(keyCode: Int, event: KeyEvent) {
        //点击删除如果在tag中间则删除整个tag
        if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
            val currentPosition = binding.editText.selectionStart
            val key = currentSelectionIsTag(currentPosition)
            if (currentPosition > 0 && key != null) {
                val tag = tags[key]
                binding.editText.text.delete(tag.selectionStart + 1, tag.selectionEnd)
                tags.remove(key)
                updateTags()
                onTagDeleteListener?.invoke(key)
            }
        }
    }

    private fun doOnTextChanged(before: Int, count: Int, text: CharSequence?, start: Int) {
        //判断输入是否在tag中间
        if (before == 0 && count > 0) {
            val addStr = text?.substring(start, start + count).orEmpty()
            val beforeLength = (text?.length ?: 0) - count
            if (start < beforeLength && !(addStr.startsWith(TAG_PREFIX) && addStr.endsWith(" ")) && currentSelectionIsTag(start) != null) {
                binding.editText.text.delete(start, start + count)
            }
        }
        //截取输入内容
        var index = 0
        tags.forEach { _, value ->
            index += value.tag?.length ?: 0
        }
        content = if (index > text.toString().length) {
            ""
        } else {
            text.toString().substring(index)
        }
    }

    /**
     * 判断光标位置是否是tag
     */
    private fun currentSelectionIsTag(currentSelection: Int): Int? {
        tags.forEach { key, value ->
            if (currentSelection in value.selectionStart..value.selectionEnd) {
                return key
            }
        }
        return null
    }

    /**
     * 添加tag
     */
    fun addTag(position: Int, tag: String?) {
        tags[position] = TagBean("$TAG_PREFIX$tag ", 0, 0)
        var selection = 0
        binding.editText.setText(buildSpannedString {
            tags.forEach { key, value ->
                color(context.color(R.color.main)) {
                    append(value.tag)
                }
                tags[key].selectionStart = selection
                selection += (value.tag?.length ?: 0)
                tags[key].selectionEnd = selection
            }
            append(content)
        })
        binding.editText.setSelection(binding.editText.text.length)
    }

    private fun updateTags() {
        var selection = 0
        tags.forEach { key, value ->
            tags[key].selectionStart = selection
            selection += (value.tag?.length ?: 0)
            tags[key].selectionEnd = selection
        }
    }

    private fun transitionCompleted() {
        if (isZoomOut) {
            postDelayed({
                decorView?.removeView(this@ZoomEditText)
                parentView?.removeView(emptyView)
                parentView?.addView(this@ZoomEditText, indexOfChild, firstLayoutParams)
            }, 30)
        }
        isZoomOut = !isZoomOut
    }

    private fun transitionStarted() {
        if (isZoomOut) {
            zoom()
        } else {
            val location = IntArray(2).also { getLocationOnScreen(it) }
            parentView?.removeView(this@ZoomEditText)
            parentView?.addView(emptyView, indexOfChild)
            decorView?.addView(this@ZoomEditText, LayoutParams(startWidth, startHeight).apply {
                topMargin = location[1]
                marginStart = location[0]
            })
            zoom()
        }
    }

    private fun zoom() {
        val anim1 = ValueAnimator.ofInt(if (isZoomOut) 0 else leftMargin, if (isZoomOut) leftMargin else 0).apply {
            duration = 200
            addUpdateListener { animator ->
                val animatedValue = animator.animatedValue as Int
                (layoutParams as MarginLayoutParams).leftMargin = animatedValue
                requestLayout()
            }
        }
        val anim2 = ValueAnimator.ofInt(if (isZoomOut) 0 else topMargin, if (isZoomOut) topMargin else 0).apply {
            duration = 200
            addUpdateListener { animator ->
                val animatedValue = animator.animatedValue as Int
                (layoutParams as MarginLayoutParams).topMargin = animatedValue
                requestLayout()
            }
        }
        val anim3 = ValueAnimator.ofInt(if (isZoomOut) endWidth else startWidth, if (isZoomOut) startWidth else endWidth).apply {
            duration = 200
            addUpdateListener { animator ->
                val animatedValue = animator.animatedValue as Int
                layoutParams.width = animatedValue
                requestLayout()
            }
        }
        val anim4 = ValueAnimator.ofInt(if (isZoomOut) endHeight else startHeight, if (isZoomOut) startHeight else endHeight).apply {
            duration = 200
            addUpdateListener { animator ->
                val animatedValue = animator.animatedValue as Int
                layoutParams.height = animatedValue
                requestLayout()
            }
        }
        val animatorSet = AnimatorSet().apply {
            playTogether(anim1, anim2, anim3, anim4)
        }
        animatorSet.start()
    }

    fun setOnTagDeleteListener(onTagDeleteListener: (Int) -> Unit) {
        this.onTagDeleteListener = onTagDeleteListener
    }

    fun getEditText() = binding.editText

    fun getTags() = tags

    data class TagBean(
        val tag: String?,
        var selectionStart: Int,
        var selectionEnd: Int
    )

}