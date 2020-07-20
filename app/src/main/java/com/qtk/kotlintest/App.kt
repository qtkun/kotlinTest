package com.qtk.kotlintest

import android.app.Application
import com.qtk.kotlintest.extensions.DelegatesExt
import com.qtk.kotlintest.method.ToastMethod
import com.qtk.kotlintest.view_model.DetailViewModel
import com.qtk.kotlintest.view_model.GoodsViewModel
import com.qtk.kotlintest.view_model.MainViewModel
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
class App : Application(){
    companion object {
        var instance : App by DelegatesExt.notNullSingleValue()
        fun instance() = instance
    }

    lateinit var fE1 : FlutterEngine
    private val viewModelModule = module {
        viewModel { MainViewModel() }
        viewModel { DetailViewModel() }
        viewModel { GoodsViewModel() }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initFE1()
        ToastMethod.registerWith(this)
        startKoin {
            androidLogger()
            androidContext(this@App)
            androidFileProperties()
            modules(listOf(viewModelModule))
        }
    }

    private fun initFE1() {
        fE1 = FlutterEngine(this)
        //可设置初始路由
//        fE1.navigationChannel.setInitialRoute("route?{\"desc\":\"点击按钮\"}")
        fE1.navigationChannel.setInitialRoute("route2")
//        fE1.navigationChannel.setInitialRoute("route3")
        fE1.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
        FlutterEngineCache
            .getInstance()
            .put("test", fE1)
    }
}