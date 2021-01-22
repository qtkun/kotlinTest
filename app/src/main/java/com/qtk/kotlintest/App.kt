package com.qtk.kotlintest

import android.app.Application
import com.qtk.kotlintest.extensions.DelegatesExt
import com.qtk.kotlintest.method.ToastMethod
import com.qtk.kotlintest.view_model.DetailViewModel
import com.qtk.kotlintest.view_model.GoodsViewModel
import com.qtk.kotlintest.view_model.MainViewModel
import dagger.hilt.android.HiltAndroidApp
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Created by qtkun
on 2020-06-16.
 */
@HiltAndroidApp
class App : Application(){
    companion object {
        var instance : App by DelegatesExt.notNullSingleValue()
    }

    lateinit var fE1 : FlutterEngine
    lateinit var fE2 : FlutterEngine
    lateinit var fE3 : FlutterEngine
    private val viewModelModule = module {
        viewModel { MainViewModel() }
        viewModel { DetailViewModel() }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initFE()
        ToastMethod.registerWith(this)
        startKoin {
            androidLogger()
            androidContext(this@App)
            androidFileProperties()
            modules(listOf(viewModelModule))
        }
    }

    private fun initFE() {
        fE1 = initEngine("route?{\"desc\":\"点击按钮\"}", "test")
        fE2 = initEngine("route2", "test2")
        fE3 = initEngine("route3", "test3")
    }

    private fun initEngine(route : String, engineId : String) : FlutterEngine{
        val fE = FlutterEngine(this)
        //可设置初始路由
        fE.navigationChannel.setInitialRoute(route)
        fE.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
        FlutterEngineCache.getInstance().put(engineId, fE)
        return fE
    }
}