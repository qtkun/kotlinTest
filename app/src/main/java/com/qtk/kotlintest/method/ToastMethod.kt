/*
package com.qtk.kotlintest.method

import android.content.Context
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class ToastMethod (private val context: Context) : MethodChannel.MethodCallHandler {
    companion object{

        @JvmStatic
        fun registerWith(context: Context) {
            val toastMethod = ToastMethod(context)
            val channel = MethodChannel(FlutterEngine(context).dartExecutor, "toast_plugin")
            channel.setMethodCallHandler(toastMethod)
        }
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when(call.method){
            "toast" -> {
                call.argument<String>("message")?.let {
                    context.toast(it)
                }
            }
        }
    }
}*/
