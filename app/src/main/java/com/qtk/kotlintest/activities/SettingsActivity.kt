package com.qtk.kotlintest.activities

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.qtk.kotlintest.R
import com.qtk.kotlintest.contant.DEFAULT_ZIP
import com.qtk.kotlintest.contant.ZIP_CODE
import com.qtk.kotlintest.extensions.DelegatesExt
import com.qtk.kotlintest.extensions.getData
import com.qtk.kotlintest.extensions.putData
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class SettingsActivity : AppCompatActivity() {
    private val dataStore by inject<DataStore<Preferences>>()
    var zipCode : Long by DelegatesExt.preference(this, ZIP_CODE, DEFAULT_ZIP)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        lifecycleScope.launchWhenCreated {
            dataStore.getData(ZIP_CODE, DEFAULT_ZIP).collectLatest {
                cityCode.setText(it.toString())
            }
        }
        intent.getStringExtra("toast")?.let { toast(it) }
        AnimatorInflater.loadAnimator(this, R.animator.scale).apply {
            setTarget(heart)
            start()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { onBackPressed(); true }
        else -> false
    }

    override fun onBackPressed() {
        lifecycleScope.launchWhenResumed {
            dataStore.putData(ZIP_CODE, cityCode.text.toString().toLong())
            zipCode = cityCode.text.toString().toLong()
            val intent = Intent()
            intent.putExtra("toast", "我从原生页面回来了")
            setResult(Activity.RESULT_OK, intent)
            super.onBackPressed()
        }
    }
}