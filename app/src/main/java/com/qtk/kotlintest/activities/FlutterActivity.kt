package com.qtk.kotlintest.activities

import android.content.Intent
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.qtk.kotlintest.App
import com.qtk.kotlintest.R
import com.qtk.kotlintest.method.IntentMethod
import io.flutter.embedding.android.FlutterFragment

class FlutterActivity : AppCompatActivity(R.layout.activity_flutter) {
    private lateinit var flutterFragment: FlutterFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        flutterFragment = FlutterFragment.withCachedEngine("test").build()
        supportFragmentManager.beginTransaction().add(R.id.fragment_layout, flutterFragment).commit()
        IntentMethod.registerWith(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            0x02 -> {
                data?.let {
                    val toast = it.getStringExtra("toast")
                    val result : MutableMap<String, Any> = HashMap()
                    toast?.let { t -> result["toast"] = t }
                    IntentMethod.channel.invokeMethod("onActivityResult", result)
                }
            }
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        flutterFragment.onPostResume()
    }

    override fun onNewIntent(@NonNull intent: Intent) {
        super.onNewIntent(intent)
        flutterFragment.onNewIntent(intent)
    }

    override fun onBackPressed() {
        flutterFragment.onBackPressed()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        flutterFragment.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onUserLeaveHint() {
        flutterFragment.onUserLeaveHint()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        flutterFragment.onTrimMemory(level)
    }
}