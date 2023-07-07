package com.qtk.kotlintest.activities

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import androidx.core.view.forEach
import com.jawnnypoo.physicslayout.PhysicsLayoutParams
import com.qtk.kotlintest.base.base.BaseVBActivity
import com.qtk.kotlintest.databinding.ActivityPhysicsBinding


class PhysicsActivity: BaseVBActivity<ActivityPhysicsBinding>() {
    private var sensorManager: SensorManager? = null
    private var gravitySensor: Sensor? = null

    private val sensorEventListener = object: SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            binding.physicsLayout.physics.setGravity(-event.values[0], event.values[1])
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    override fun ActivityPhysicsBinding.initViewBinding() {
        sensorManager = getSystemService()
        gravitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)
        binding.physicsLayout.forEach {
            (it.layoutParams as PhysicsLayoutParams).config.bodyDef.allowSleep = false
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(sensorEventListener)
    }
}