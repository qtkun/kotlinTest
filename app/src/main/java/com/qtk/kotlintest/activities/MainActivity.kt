package com.qtk.kotlintest.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.qtk.kotlintest.adapter.ForecastListAdapter
import com.qtk.kotlintest.R
import com.qtk.kotlintest.view_model.MainViewModel
import com.qtk.kotlintest.adapter.update
import com.qtk.kotlintest.databinding.ActivityMainBinding
import com.qtk.kotlintest.domain.command.RequestForecastCommand
import com.qtk.kotlintest.domain.model.ForecastList
import com.qtk.kotlintest.extensions.DelegatesExt
import com.qtk.kotlintest.extensions.inflate
import com.qtk.kotlintest.extensions.toPx
import com.qtk.kotlintest.method.IntentMethod
import com.qtk.kotlintest.test.Truck
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() , ToolbarManager {
    override val toolbar by lazy { binding.toolbar.toolbar }
    override val activity: Activity by lazy { this }
    var zipCode : Long by DelegatesExt.preference(this,
        SettingsActivity.ZIP_CODE,
        SettingsActivity.DEFAULT_ZIP
    )
    private var adapter : ForecastListAdapter? = null
    lateinit var city : String
    //koin依赖注入
    private val mViewModel by viewModel<MainViewModel>()
    val gson by inject<Gson>()

    //hilt依赖注入
    @Inject
    lateinit var truck: Truck
    /*@Inject
    lateinit var gson: Gson*/

    val binding by inflate<ActivityMainBinding>()

    private val dialog: Dialog by lazy {
        Dialog(this).apply {
            setContentView(R.layout.loading_dialog)
            setCanceledOnTouchOutside(false)
            window?.also {
                it.setBackgroundDrawableResource(R.drawable.loading_bg)
                it.setGravity(Gravity.CENTER)
                it.setLayout(150.0.toPx(), 100.0.toPx())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.forecastList.layoutManager = LinearLayoutManager(this)
        attachToScroll(binding.forecastList)
        adapter = ForecastListAdapter(mViewModel.forecastList.value?.dailyForecast) {
            this.startActivity<DetailActivity>(
                DetailActivity.ID to it.id,
                DetailActivity.CITY_NAME to city
            )
        }
        binding.forecastList.adapter = adapter
        lifecycleScope.launchWhenCreated {
            mViewModel.forecastList.observe(this@MainActivity, observer())
            mViewModel.loading.observe(this@MainActivity, Observer {
                if (it) {
                    dialog.show()
                } else {
                    if (dialog.isShowing) dialog.dismiss()
                }
            })
            truck.deliver()
        }
    }

    private fun observer(): Observer<ForecastList> {
        return Observer { result ->
            city = result.city
            adapter?.update(result.dailyForecast)
            toolbarTitle = "${result.city} (${result.country})"
            Log.i("qtk", gson.toJson(result))
        }
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IntentMethod.RequestCode) {
            data?.getStringExtra("toast")?.let { toast(it) }
        }
    }

    //协程对liveCycle
    private fun load() = lifecycleScope.launchWhenResumed {
        if (lifecycle.currentState != Lifecycle.State.DESTROYED){
            mViewModel.setData2(zipCode)
        }
    }

    //协程对liveData
    private fun load3() = liveData {
        emit(RequestForecastCommand(zipCode).execute())
    }.observe(this, observer())

    private fun load2() = lifecycleScope.launchWhenResumed {
        val result = RequestForecastCommand(zipCode).execute()
        city = result.city
        if (adapter == null) {
            adapter =
                ForecastListAdapter(result.dailyForecast) {
                    ctx.startActivity<DetailActivity>(
                        DetailActivity.ID to it.id,
                        DetailActivity.CITY_NAME to city
                    )
                }
            binding.forecastList.adapter = adapter
        } else {
            adapter?.update(result.dailyForecast)
//                adapter?.update() { adapter?.items = result.dailyForecast}
        }
        toolbarTitle = "${result.city} (${result.country})"
    }
}
