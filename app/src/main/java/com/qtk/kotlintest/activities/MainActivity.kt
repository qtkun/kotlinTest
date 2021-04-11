package com.qtk.kotlintest.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.google.gson.Gson
import com.qtk.kotlintest.App
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.ForecastListAdapter2
import com.qtk.kotlintest.base.update
import com.qtk.kotlintest.contant.BITMAP_ID
import com.qtk.kotlintest.view_model.MainViewModel
import com.qtk.kotlintest.contant.DEFAULT_ZIP
import com.qtk.kotlintest.contant.ZIP_CODE
import com.qtk.kotlintest.contant.locationPermission
import com.qtk.kotlintest.databinding.ActivityMainBinding
import com.qtk.kotlintest.domain.command.RequestForecastCommand
import com.qtk.kotlintest.domain.model.ForecastList
import com.qtk.kotlintest.extensions.*
import com.qtk.kotlintest.method.IntentMethod
import com.qtk.kotlintest.utils.dateToPosition
import com.qtk.kotlintest.utils.getMonthDate
import com.qtk.kotlintest.utils.map.TraceAsset
import com.qtk.kotlintest.utils.positionToDate
import com.qtk.kotlintest.work.LocationWorker
import com.qtk.kotlintest.work.SaveImageWorker
import com.qtk.kotlintest.work.TestWork
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.anko.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ToolbarManager {
    override val toolbar by lazy { binding.toolbar.toolbar }
    override val activity: Activity by lazy { this }
    var zipCode: Long by DelegatesExt.preference(this, ZIP_CODE, DEFAULT_ZIP)
    private val adapter: ForecastListAdapter2 by lazy {
        ForecastListAdapter2(mViewModel.forecastList.value?.dailyForecast) { forecast, _ ->
            this.startActivity<DetailActivity>(
                DetailActivity.ID to forecast.id,
                DetailActivity.CITY_NAME to city
            )
        }
    }
    lateinit var city: String

    //koin依赖注入
    private val mViewModel by viewModel<MainViewModel>()
    private val gson by inject<Gson>()

    //hilt依赖注入
    /*@Inject
    lateinit var truck: Truck
    @Inject
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

    private val backgroundLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        it?.let {
            /*WorkManager.getInstance(this@MainActivity)
                .enqueue(locationWorker)*/
        }
    }

    private val coarseLocation = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
            it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                backgroundLocation.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                /*WorkManager.getInstance(this@MainActivity)
                    .enqueue(locationWorker)*/
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.forecastList.layoutManager = LinearLayoutManager(this)
        attachToScroll(binding.forecastList)
        binding.forecastList.adapter = adapter
        coarseLocation.launch(locationPermission)
        business()
    }

    private fun business() {
        lifecycleScope.launchWhenCreated {
            mViewModel.forecastList.observe(this@MainActivity, observer())
            mViewModel.loading.observe(this@MainActivity, Observer {
                if (it) {
                    dialog.show()
                } else {
                    if (dialog.isShowing) dialog.dismiss()
                }
            })
            println(getMonthDate(positionToDate(dateToPosition())))
            /*WorkManager.getInstance(this@MainActivity)
                .beginWith(saveImageWorkRequest)
                .then(testWorkRequest).
                enqueue()

            WorkManager.getInstance(this@MainActivity)
                .getWorkInfoByIdLiveData(saveImageWorkRequest.id)
                .observe(this@MainActivity, Observer {
                    when(it.state){
                        WorkInfo.State.ENQUEUED -> toast("开始保存")
                        WorkInfo.State.RUNNING -> toast("正在保存中")
                        WorkInfo.State.SUCCEEDED -> toast("保存成功")
                        WorkInfo.State.FAILED -> toast("保存失败")
                        WorkInfo.State.BLOCKED -> toast("挂起")
                        WorkInfo.State.CANCELLED -> toast("取消保存")
                    }
                })*/

            /*mViewModel.getZipCode().observe(this@MainActivity, Observer {
                lifecycleScope.launchWhenResumed {
                    mViewModel.setData2(it)
                }
            })*/
        }
    }

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()

    val data = workDataOf(
        BITMAP_ID to R.mipmap.image_bg
    )

    private val saveImageWorkRequest: OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<SaveImageWorker>()
            .setInputData(data)
            .build()

    private val testWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<TestWork>()
        .setInitialDelay(1, TimeUnit.SECONDS)
        .build()

    private val testWorkRequest2: WorkRequest =
        PeriodicWorkRequestBuilder<TestWork>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

    private val locationWorker: WorkRequest =
        OneTimeWorkRequestBuilder<LocationWorker>()
            .setConstraints(constraints)
            .build()

    private fun observer(): Observer<ForecastList> = Observer { result ->
        city = result.city
        adapter.update(result.dailyForecast)
        toolbarTitle = "${result.city} (${result.country})"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IntentMethod.RequestCode -> data?.getStringExtra("toast")?.let { toast(it) }
                PICK_FILE -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        val uri = data.data
                        if (uri != null) {
                            val inputStream = contentResolver.openInputStream(uri)
                            // 执行文件读取操作
                        }
                    }
                }
            }

        }
    }

    //协程对liveCycle
    private fun load() = lifecycleScope.launchWhenResumed {
        if (lifecycle.currentState != Lifecycle.State.DESTROYED) {
            mViewModel.setData(zipCode)
        }
    }

    //协程对liveData
    private fun load2() = liveData {
        emit(RequestForecastCommand(zipCode).execute())
    }.observe(this, observer())

    fun pickFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE)
    }

    companion object {
        const val PICK_FILE = 0x99
    }
}
