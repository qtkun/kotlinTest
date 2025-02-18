package com.qtk.kotlintest.activities

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.result.contract.ActivityResultContracts
import androidx.bluetooth.BluetoothLe
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtk.kotlintest.adapter.BluetoothAdapterProxy
import com.qtk.kotlintest.base.base.BaseVBActivity
import com.qtk.kotlintest.base.base.MultiAdapter
import com.qtk.kotlintest.databinding.ActivityBluetoothBinding
import kotlinx.coroutines.launch

class BluetoothActivity: BaseVBActivity<ActivityBluetoothBinding>() {
    private val adapter by lazy {
        MultiAdapter(listOf(BluetoothAdapterProxy()))
    }

    private val bluetoothLe by lazy {
        BluetoothLe(this)
    }

    private val bluetoothPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        var success = true
        it.values.forEach {
            if (!it) {
                success = false
            }
        }
        if (success) {
            scan()
        }
    }

    private val permission = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun ActivityBluetoothBinding.initViewBinding() {
        rvBluetooth.layoutManager = LinearLayoutManager(this@BluetoothActivity)
        rvBluetooth.adapter = adapter

        bluetoothPermission.launch(permission)
    }

    private fun scan() {
        lifecycleScope.launch {
            bluetoothLe.scan().collect {
                it.device.name?.let { name ->
                    adapter.addData(name)
                }
            }
        }
    }
}