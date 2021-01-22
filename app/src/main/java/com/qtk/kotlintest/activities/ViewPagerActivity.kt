package com.qtk.kotlintest.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.ViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_viewpager2.*

@AndroidEntryPoint
class ViewPagerActivity : AppCompatActivity(R.layout.activity_viewpager2) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view_pager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(tab_layout, view_pager
        ) { tab, position -> // Styling each tab here
            when(position){
                0 -> tab.text = "商品档案"
                1 -> tab.text = "套餐档案"
            }
        }.attach()
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> title_tv.text = "商品档案"
                    1 -> title_tv.text = "套餐档案"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0 -> title_tv.text = "商品档案"
                    1 -> title_tv.text = "套餐档案"
                }
            }
        })
        back.setOnClickListener {
            finish()
        }
    }
}