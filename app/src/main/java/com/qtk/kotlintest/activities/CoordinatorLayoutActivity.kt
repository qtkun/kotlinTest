package com.qtk.kotlintest.activities

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.ViewPagerAdapter
import com.qtk.kotlintest.databinding.ActivityCoordinatorBinding
import com.qtk.kotlintest.databinding.HomeTabLayoutBinding
import com.qtk.kotlintest.extensions.drawable
import com.qtk.kotlintest.extensions.inflate
import com.zackratos.ultimatebarx.ultimatebarx.statusBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoordinatorLayoutActivity: AppCompatActivity(R.layout.activity_coordinator) {
    val binding by inflate<ActivityCoordinatorBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBar {
            transparent()
            light = true
        }
        /*binding.homeViewpager.adapter = ViewPagerAdapter(this)
        with(binding) {
            homeViewpager.adapter = ViewPagerAdapter(this@CoordinatorLayoutActivity)
            TabLayoutMediator(
                homeCategoryTab, homeViewpager
            ) { tab, position -> // Styling each tab here
                val binding =
                    HomeTabLayoutBinding.inflate(LayoutInflater.from(this@CoordinatorLayoutActivity))
                when (position) {
                    0 -> {
                        binding.icon = drawable(R.drawable.workbench_selector)
                        binding.label = "工作台"
                        tab.customView = binding.root
                        *//*tab.icon = itemView.context.drawable(R.drawable.ic_workbench_selected)
                        tab.text = "列表1"*//*
                    }
                    1 -> {
                        binding.icon = drawable(R.drawable.customer_selector)
                        binding.label = "客户"
                        tab.customView = binding.root
                        *//*tab.icon = itemView.context.drawable(R.drawable.ic_customer_unselected)
                        tab.text = "列表2"*//*
                    }
                }
            }.attach()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }*/
    }
}