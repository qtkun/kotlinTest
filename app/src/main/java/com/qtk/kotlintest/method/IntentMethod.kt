/*
package com.qtk.kotlintest.method

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import com.qtk.kotlintest.App
import com.qtk.kotlintest.activities.SettingsActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


class IntentMethod(private val activity: Activity) : MethodChannel.MethodCallHandler {
    companion object{
        const val RequestCode = 0x01
        lateinit var channel : MethodChannel

        @JvmStatic
        fun registerWith(activity: Activity, engineId : String) {
            val intentMethod = IntentMethod(activity)
            when (engineId) {
                "test" -> channel = MethodChannel(App.instance.fE1.dartExecutor, "intent_plugin")
                "test2" -> channel = MethodChannel(App.instance.fE2.dartExecutor, "intent_plugin")
                "test3" -> channel = MethodChannel(App.instance.fE3.dartExecutor, "intent_plugin")
            }
            channel.setMethodCallHandler(intentMethod)
        }
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when(call.method){
            "jumpToSetting" -> {
                activity.startActivity<SettingsActivity>(
                    "toast" to call.argument<Any>("toast").toString()
                )
            }
            "jumpToSettingForResult" -> {
                call.argument<Int>("REQUEST_CODE")?.let {
                    activity.startActivityForResult<SettingsActivity>(it,
                        "toast" to call.argument<Any>("toast").toString()
                    )
                }
            }
            "goBackWithResult" -> {
                val backIntent = Intent()
                backIntent.putExtra("toast", call.argument<String>("toast"))
                activity.setResult(RESULT_OK, backIntent)
                activity.finish()
            }
        }
    }
}*/
