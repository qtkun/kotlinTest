package com.qtk.kotlintest.method

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import com.qtk.kotlintest.App
import com.qtk.kotlintest.activities.SettingsActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult


class IntentMethod(private val activity: Activity) : MethodChannel.MethodCallHandler {
    companion object{
        const val RequestCode = 0x01
        val channel = MethodChannel(App.instance.fE1.dartExecutor, "intent_plugin")

        @JvmStatic
        fun registerWith(activity: Activity) {
            val intentMethod = IntentMethod(activity)
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
}