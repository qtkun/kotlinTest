package com.qtk.kotlintest.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtk.kotlintest.adapter.ParentAdapter
import com.qtk.kotlintest.databinding.ActivityViewpager2NestBinding
import com.qtk.kotlintest.extensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPagerActivity : AppCompatActivity() {
    private val binding by viewBinding<ActivityViewpager2NestBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.parent.layoutManager = LinearLayoutManager(this)
        binding.parent.adapter = ParentAdapter(this)
        binding.back.setOnClickListener {
            finish()
        }
        /*binding.viewPager.adapter = ViewPagerAdapter(this@ViewPagerActivity)
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
        }*/
    }
}