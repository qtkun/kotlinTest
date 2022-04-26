package com.qtk.kotlintest.activities

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.adapter.PokemonAdapter2
import com.qtk.kotlintest.base.base.BaseActivity
import com.qtk.kotlintest.databinding.ActivityAnimTestBinding
import com.qtk.kotlintest.extensions.dpToPx
import com.qtk.kotlintest.extensions.getScreenWidth
import com.qtk.kotlintest.retrofit.data.PokemonBean
import com.qtk.kotlintest.view_model.AnimTestViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnimTestActivity: BaseActivity<ActivityAnimTestBinding, AnimTestViewModel>() {

    private val trvHeight = 15f.dpToPx()

    private val adapter by lazy {
        PokemonAdapter2{ position, item ->
            selectItem(position, item)
        }
    }

    private val layoutManager by lazy {
        LinearLayoutManager(this@AnimTestActivity, RecyclerView.HORIZONTAL, false)
    }

    private fun selectItem(position: Int, item: PokemonBean) {
        for ((pos, data) in adapter.currentList.withIndex()) {
            if (data.like && pos == position) return
        }
        adapter.currentList.forEach {
            it.like = item.id == it.id
        }
        adapter.notifyDataSetChanged()

        binding.rv.post{
            setTrvAnim(position)
        }
    }

    private fun setTrvAnim(position: Int) {
        getItemCenterX(position)?.let { x ->
            val offset = (binding.tc.radius + binding.tc.trvWidth).toInt()
            if (x < offset || x > getScreenWidth() - offset) {
                setTrvHeight(0f)
            } else {
                if (binding.tc.trvHeight > 0f) {
                    trvHideAnim = trvHideAnim()?.apply {
                        doOnEnd {
                            trvShowAnim(x).start()
                            trvHideAnim = null
                        }
                    }
                } else {
                    trvShowAnim(x).start()
                }
                trvHideAnim?.start()
            }
            return
        }
        setTrvHeight(0f)
    }

    private fun getItemCenterX(position: Int): Int? {
        binding.rv.findViewHolderForAdapterPosition(position)?.itemView?.let { view ->
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            return location[0] + view.measuredWidth / 2 + 12.dpToPx()
        }
        return null
    }

    private fun setTrvHeight(height: Float) {
        binding.tc.trvHeight = height
        binding.tc.layoutParams = (binding.tc.layoutParams as ViewGroup.MarginLayoutParams).apply {
            topMargin = (trvHeight - height).toInt() + 18.dpToPx()
        }
    }

    private var trvHideAnim: ValueAnimator? = null

    private fun trvHideAnim(): ValueAnimator? {
        if (trvHideAnim?.isRunning == true) return null
        return ValueAnimator.ofFloat(trvHeight, 0f).apply {
            duration = 100L
            addUpdateListener {
                setTrvHeight(it.animatedValue as Float)
            }
        }
    }

    private fun trvShowAnim(x: Int): ValueAnimator {
        binding.tc.padding = (getScreenWidth() - x).toFloat()
        return ValueAnimator.ofFloat(0f, trvHeight).apply {
            duration = 100L
            addUpdateListener {
                setTrvHeight(it.animatedValue as Float)
            }
        }
    }

    override fun ActivityAnimTestBinding.initViewBinding() {
        rv.apply {
            adapter = this@AnimTestActivity.adapter
            layoutManager = this@AnimTestActivity.layoutManager
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        for ((pos, data) in this@AnimTestActivity.adapter.currentList.withIndex()) {
                            if (data.like) {
                                getItemCenterX(pos).let { x ->
                                    val offset = (binding.tc.radius + binding.tc.trvWidth).toInt()
                                    if (x == null || x < offset || x > getScreenWidth() - offset) {
                                        setTrvHeight(0f)
                                    } else {
                                        trvShowAnim(x).start()
                                    }
                                }
                                break
                            }
                        }
                    } else {
                        if (binding.tc.trvHeight != 0f) {
                            trvHideAnim = trvHideAnim()?.apply {
                                doOnEnd {
                                    trvHideAnim = null
                                }
                            }
                            trvHideAnim?.start()
                        }
                    }
                }

            })
        }
    }

    override fun AnimTestViewModel.initViewModel() {
        lifecycleScope.launch {
            viewModel.list
                .filter { it.isNotEmpty() }
                .collectLatest {
                    adapter.submitList(it)
                    selectItem(0, it[0])
                }
        }
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        super.initVariables(savedInstanceState)
        viewModel.getPokemon(10, 0)
    }
}