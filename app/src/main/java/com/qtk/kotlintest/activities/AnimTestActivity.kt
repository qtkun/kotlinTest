package com.qtk.kotlintest.activities

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.adapter.PokemonAdapterProxy
import com.qtk.kotlintest.base.base.BaseActivity
import com.qtk.kotlintest.base.base.MultiTypeListAdapter
import com.qtk.kotlintest.databinding.ActivityAnimTestBinding
import com.qtk.kotlintest.extensions.asDp
import com.qtk.kotlintest.extensions.getScreenWidth
import com.qtk.kotlintest.extensions.setOnItemClickListener
import com.qtk.kotlintest.retrofit.data.PokemonBean
import com.qtk.kotlintest.view_model.AnimTestViewModel
import com.qtk.kotlintest.widget.SpringEdgeEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.jetbrains.anko.startActivity

@AndroidEntryPoint
class AnimTestActivity: BaseActivity<ActivityAnimTestBinding, AnimTestViewModel>() {

    private val trvHeight = 15f.asDp()

    private val adapter by lazy {
        MultiTypeListAdapter(listOf(PokemonAdapterProxy()), itemClick = {
            selectItem(it)
        })
    }

    private val layoutManager by lazy {
        LinearLayoutManager(this@AnimTestActivity, RecyclerView.HORIZONTAL, false)
    }

    private fun selectItem(position: Int) {
        val item = adapter.currentList[position]
        if (item !is PokemonBean) return
        for ((pos, data) in adapter.currentList.withIndex()) {
            if (data is PokemonBean && data.like && pos == position) return
        }
        adapter.currentList.forEach {
            if (it is PokemonBean) it.like = item.id == it.id
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
            return location[0] + view.measuredWidth / 2 + 12.asDp()
        }
        return null
    }

    private fun setTrvHeight(height: Float) {
        binding.tc.trvHeight = height
        binding.tc.layoutParams = (binding.tc.layoutParams as ViewGroup.MarginLayoutParams).apply {
            topMargin = (trvHeight - height).toInt() + 18.asDp()
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
        stickLayout.setStartActivity {
            startActivity<MotionActivity>()
        }
        rv.apply {
            adapter = this@AnimTestActivity.adapter
            layoutManager = this@AnimTestActivity.layoutManager
            setOnItemClickListener { view, i ->
                selectItem(i)
            }
            edgeEffectFactory = SpringEdgeEffect()
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        for ((pos, data) in this@AnimTestActivity.adapter.currentList.withIndex()) {
                            if (data is PokemonBean && data.like) {
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
                    adapter.submitList(mutableListOf<Any>().apply {
                        addAll(it)
                    })
                    selectItem(0)
                }
        }
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        super.initVariables(savedInstanceState)
        viewModel.getPokemon(10, 0)
    }
}