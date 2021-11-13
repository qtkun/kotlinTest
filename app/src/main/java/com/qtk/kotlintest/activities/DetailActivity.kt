package com.qtk.kotlintest.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.qtk.kotlintest.view_model.DetailViewModel
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.BaseActivity
import com.qtk.kotlintest.base.initLoading
import com.qtk.kotlintest.databinding.ActivityDetailBinding
import com.qtk.kotlintest.domain.command.RequestDayForecastCommand
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.extensions.color
import com.qtk.kotlintest.extensions.drawable
import com.qtk.kotlintest.extensions.textColor
import com.qtk.kotlintest.extensions.toDateString
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat

class DetailActivity :BaseActivity<ActivityDetailBinding>(R.layout.activity_detail), ToolbarManager {
    companion object{
        const val ID = "DetailActivity:id"
        const val CITY_NAME = "DetailActivity:cityName"
    }

    override val toolbar by lazy { binding.toolbar.toolbar }
    override val activity: Activity by lazy { this }
    private val detailViewModel by viewModel<DetailViewModel>()
    private lateinit var textAnim: AnimatedVectorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLoading(this, detailViewModel)
        lifecycleScope.launch {
            detailViewModel.detail2.collectLatest {
                supportActionBar?.subtitle = it.date.toDateString(DateFormat.FULL)
                bindWeather(it.high to binding.maxTemperature, it.low to binding.minTemperature)
            }
        }
        initToolbar()
        toolbarTitle = intent.getStringExtra(CITY_NAME) ?: ""
        enableHomeAsUp {
            onBackPressed()
        }
        toolbar.logo = drawable(R.drawable.test_anim)
        textAnim = toolbar.logo as AnimatedVectorDrawable

        lifecycleScope.launchWhenResumed{
//            detailViewModel.getDetail(lifecycle, intent.getLongExtra(ID, -1))
            detailViewModel.id.value = intent.getLongExtra(ID, -1)
//            bindForecast(loadLots())
        }
    }

    //结构化并发
    private suspend fun loadLots() : Forecast = coroutineScope {
        launch { RequestDayForecastCommand(intent.getLongExtra(ID, -1)).execute() }
        launch { RequestDayForecastCommand(intent.getLongExtra(ID, -1)).execute() }
        launch { RequestDayForecastCommand(intent.getLongExtra(ID, -1)).execute() }
        withContext(lifecycleScope.coroutineContext) { RequestDayForecastCommand(intent.getLongExtra(ID, -1)).execute() }
    }

    @SuppressLint("SetTextI18n")
    private fun bindWeather(vararg views: Pair<Int, TextView>) = views.forEach {
        it.second.textColor = color(when (it.first) {
            in -50..0 -> android.R.color.holo_red_dark
            in 0..15 -> android.R.color.holo_orange_dark
            else -> android.R.color.holo_green_dark
        })
    }
}