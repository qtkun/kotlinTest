package com.qtk.kotlintest.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.ActivityCoordinatorBinding
import com.qtk.kotlintest.extensions.inflate

class CoordinatorLayoutActivity: AppCompatActivity(R.layout.activity_coordinator) {
    val binding by inflate<ActivityCoordinatorBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}