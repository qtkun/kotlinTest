package com.qtk.kotlintest.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.qtk.kotlintest.view_model.DetailViewModel
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.ActivityDetailBinding
import com.qtk.kotlintest.domain.command.RequestDayForecastCommand
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.extensions.color
import com.qtk.kotlintest.extensions.textColor
import com.qtk.kotlintest.extensions.toDateString
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import org.jetbrains.anko.find
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat

class DetailActivity :AppCompatActivity(), ToolbarManager {
    companion object{
        const val ID = "DetailActivity:id"
        const val CITY_NAME = "DetailActivity:cityName"
    }

    override val toolbar by lazy<Toolbar> { find(R.id.toolbar) }
    override val activity: Activity by lazy { this }
    private val detailViewModel by viewModel<DetailViewModel>()
    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        detailViewModel.detail.observe(this, Observer { bindForecast(it) })
        initToolbar()
        toolbarTitle = intent.getStringExtra(CITY_NAME) ?: ""
        enableHomeAsUp { onBackPressed() }
        lifecycleScope.launchWhenResumed{
//            detailViewModel.setDetail(intent.getLongExtra(ID, -1))
            bindForecast(loadLots())
        }
    }

    //结构化并发
    private suspend fun loadLots() : Forecast = coroutineScope {
        launch { RequestDayForecastCommand(intent.getLongExtra(ID, -1)).execute() }
        launch { RequestDayForecastCommand(intent.getLongExtra(ID, -1)).execute() }
        launch { RequestDayForecastCommand(intent.getLongExtra(ID, -1)).execute() }
        withContext(lifecycleScope.coroutineContext) { RequestDayForecastCommand(intent.getLongExtra(ID, -1)).execute() }
    }

    private fun bindForecast(forecast: Forecast) = with(forecast) {
        Picasso.get().load(iconUrl).into(binding.icon)
        supportActionBar?.subtitle = date.toDateString(DateFormat.FULL)
        binding.weatherDescription.text = description
        bindWeather(high to binding.maxTemperature, low to binding.minTemperature)
    }

    @SuppressLint("SetTextI18n")
    private fun bindWeather(vararg views: Pair<Int, TextView>) = views.forEach {
        it.second.text = "${it.first}°"
        it.second.textColor = color(when (it.first) {
            in -50..0 -> android.R.color.holo_red_dark
            in 0..15 -> android.R.color.holo_orange_dark
            else -> android.R.color.holo_green_dark
        })
    }
}