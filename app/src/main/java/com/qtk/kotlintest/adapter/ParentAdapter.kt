package com.qtk.kotlintest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.HomeTabLayoutBinding
import com.qtk.kotlintest.databinding.ItemNestBottomBinding
import com.qtk.kotlintest.databinding.ItemNestTopBinding
import com.qtk.kotlintest.extensions.bindItemView
import com.qtk.kotlintest.extensions.dpToPx
import com.qtk.kotlintest.extensions.drawable
import com.qtk.kotlintest.extensions.getScreenWidth
import org.jetbrains.anko.toast

class ParentAdapter(val activity: AppCompatActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TOP = 0
        private const val VIEW_TYPE_BOTTOM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_TOP -> {
                val itemView = LayoutInflater.from(activity).inflate(R.layout.item_nest_top, parent, false)
                TopViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(activity).inflate(R.layout.item_nest_bottom, parent, false)
                BottomViewHolder(activity, itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = 2

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_TOP
        } else {
            VIEW_TYPE_BOTTOM
        }
    }
}

class TopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding by bindItemView<ItemNestTopBinding>(itemView)
}

class BottomViewHolder(activity: AppCompatActivity, itemView: View): RecyclerView.ViewHolder(itemView) {
    private val binding by bindItemView<ItemNestBottomBinding>(itemView)
    init {
        with(binding){
            viewPager.adapter = ViewPagerAdapter(activity)
            val padding = (activity.getScreenWidth() - 240.dpToPx()) / 2
            (viewPager.getChildAt(0) as RecyclerView).apply {
                clipToPadding = false
                setPadding(padding, 0, padding, 0)
            }
            TabLayoutMediator(
                tabLayout, viewPager
            ) { tab, position -> // Styling each tab here
                val binding = HomeTabLayoutBinding.inflate(LayoutInflater.from(itemView.context))
                when (position) {
                    0 -> {
                        binding.icon = itemView.context.drawable(R.drawable.workbench_selector)
                        binding.label = "工作台"
                        tab.customView = binding.root
                        /*tab.icon = itemView.context.drawable(R.drawable.ic_workbench_selected)
                        tab.text = "列表1"*/
                    }
                    1 -> {
                        binding.icon = itemView.context.drawable(R.drawable.customer_selector)
                        binding.label = "客户"
                        tab.customView = binding.root
                        /*tab.icon = itemView.context.drawable(R.drawable.ic_customer_unselected)
                        tab.text = "列表2"*/
                    }
                }
            }.attach()
            /*tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            tab.icon = itemView.context.drawable(R.drawable.ic_workbench_selected)
                        }
                        1 -> {
                            tab.icon = itemView.context.drawable(R.drawable.ic_customer_selected)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            tab.icon = itemView.context.drawable(R.drawable.ic_workbench_unselected)
                        }
                        1 -> {
                            tab.icon = itemView.context.drawable(R.drawable.ic_customer_unselected)
                        }
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })*/
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> activity.toast("列表1")
                        1 -> activity.toast("列表2")
                    }
                }
            })
        }
    }
}