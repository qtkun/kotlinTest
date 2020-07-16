package com.qtk.kotlintest.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.qtk.kotlintest.R
import com.qtk.kotlintest.extensions.DelegatesExt
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val ZIP_CODE = "zipCode"
        const val DEFAULT_ZIP = 94043L
    }

    var zipCode : Long by DelegatesExt.preference(this, ZIP_CODE, DEFAULT_ZIP)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        cityCode.setText(zipCode.toString());
        intent.getStringExtra("toast")?.let { toast(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { onBackPressed(); true }
        else -> false
    }

    override fun onBackPressed() {
        zipCode = cityCode.text.toString().toLong()
        val intent = Intent()
        intent.putExtra("toast", "我从原生页面回来了")
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }
}