package com.qtk.kotlintest.method

import android.content.Context
import com.qtk.kotlintest.App
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.jetbrains.anko.toast

class ToastMethod (private val context: Context) : MethodChannel.MethodCallHandler {
    companion object{
        private val channel = MethodChannel(App.instance.fE1.dartExecutor, "toast_plugin")

        @JvmStatic
        fun registerWith(context: Context) {
            val toastMethod = ToastMethod(context)
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
}