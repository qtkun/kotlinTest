package com.qtk.kotlintest.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.qtk.kotlintest.adapter.ViewPagerAdapter
import com.qtk.kotlintest.databinding.ActivityViewpager2Binding
import com.qtk.kotlintest.extensions.inflate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPagerActivity : AppCompatActivity() {
    private val binding by inflate<ActivityViewpager2Binding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewPager.adapter = ViewPagerAdapter(this@ViewPagerActivity)
        with(binding){
            viewPager.adapter = ViewPagerAdapter(this@ViewPagerActivity)
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
                        0 -> titleTv.text = "列表1"
                        1 -> titleTv.text = "列表2"
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> titleTv.text = "列表1"
                        1 -> titleTv.text = "列表2"
                    }
                }
            })
            back.setOnClickListener {
                finish()
            }
        }
    }
}