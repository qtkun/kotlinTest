package com.qtk.kotlintest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.ItemNestBottomBinding
import com.qtk.kotlintest.databinding.ItemNestTopBinding
import com.qtk.kotlintest.extensions.bindItemView
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
        binding.viewPager.adapter = ViewPagerAdapter(activity)
        with(binding){
            viewPager.adapter = ViewPagerAdapter(activity)
            TabLayoutMediator(
                tabLayout, viewPager
            ) { tab, position -> // Styling each tab here
                when (position) {
                    0 -> tab.text = "列表1"
                    1 -> tab.text = "列表2"
                }
            }.attach()
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> activity.toast("列表1")
                        1 -> activity.toast("列表2")
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
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