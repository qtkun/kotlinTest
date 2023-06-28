package com.qtk.kotlintest.activities

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.qtk.flowbus.post.postEvent
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.ActivityCoordinatorBinding
import com.qtk.kotlintest.extensions.*
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX
import com.zackratos.ultimatebarx.ultimatebarx.statusBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoordinatorLayoutActivity: AppCompatActivity(R.layout.activity_coordinator) {
    val binding by inflate<ActivityCoordinatorBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBar {
            transparent()
        }
        with(binding) {
            UltimateBarX.addStatusBarTopPadding(topBar)
            Glide.with(this@CoordinatorLayoutActivity)
                .load("https://img0.baidu.com/it/u=3957758939,1600769248&fm=253&fmt=auto&app=120&f=JPEG?w=1280&h=800")
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        resource?.let {
                            detectBitmapColor(it.toBitmap(), topBar.width, topBar.height)
                        }
                        return false
                    }
                })
                .into(ivBackground)

            tbBtn.singleClick {
                postEvent("platform", "淘宝")
            }
            jdBtn.singleClick {
                postEvent("platform", "京东")
            }
            mtBtn.singleClick {
                postEvent("platform", "美团")
            }
            elmBtn.singleClick {
                postEvent("platform", "饿了么")
            }
            noobBtn.singleClick {
                postEvent("platform", "新手攻略")
            }
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

    private fun detectBitmapColor(bitmap: Bitmap, right: Int, bottom: Int) {
        val colorCount = 5
        Palette.from(bitmap)
            .setRegion(0, 0, right, bottom)
            .maximumColorCount(colorCount)
            .generate {
                it?.let { palette ->
                    var mostPopularSwatch: Palette.Swatch? = null
                    for (swatch in palette.swatches) {
                        if (mostPopularSwatch == null || swatch.population > mostPopularSwatch.population) {
                            mostPopularSwatch = swatch
                        }
                    }
                    mostPopularSwatch?.let { swatch ->
                        Log.e("CoordinatorLayoutActivity", swatch.toString())
                        val luminance = ColorUtils.calculateLuminance(swatch.rgb)
                        statusBar {
                            transparent()
                            light = luminance > 0.5
                        }
                        binding.ivBack.imageTintList = ColorStateList.valueOf(swatch.bodyTextColor)
                        binding.tvTitle.setTextColor(swatch.bodyTextColor)
                    }
                }
            }
    }
}