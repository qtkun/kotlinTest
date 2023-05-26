package com.qtk.kotlintest.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.qtk.kotlintest.App
import com.qtk.kotlintest.base.base.BaseActivity
import com.qtk.kotlintest.base.base.BaseViewModel
import com.qtk.kotlintest.contant.bluetoothPermission
import com.qtk.kotlintest.databinding.ActivityBleBinding
import java.util.UUID


class BleActivity: BaseActivity<ActivityBleBinding, BaseViewModel>() {
    companion object {
        private const val TAG = "BLEManager"
        private val SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private val CHARACTERISTIC_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null
    private var mBluetoothGatt: BluetoothGatt? = null

    private val mScanCallback = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                connectToDevice(it.device)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }
    }

    private val mGattCallback = object: BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    if (ActivityCompat.checkSelfPermission(this@BleActivity,
                            Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        gatt?.discoverServices()
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                readCharacteristic()
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val value = characteristic?.value
                // 处理读取到的特性值
            }
        }
    }

    private val bluetoothConnect = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.BLUETOOTH_CONNECT] == true) {
            connectBluetooth()
        }
    }

    private val bluetoothConnectDevice = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.BLUETOOTH_CONNECT] == true) {
            connectBluetooth()
        }
    }

    private val bluetoothInit = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.BLUETOOTH] == true &&
                it[Manifest.permission.BLUETOOTH_ADMIN] == true &&
                it[Manifest.permission.BLUETOOTH_CONNECT] == true) {
            initBLE()
        }
    }

    private val bluetoothScan = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.BLUETOOTH_SCAN] == true) {
            startScan()
        }
    }

    private fun connectBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent) {

        }
    }

    override fun ActivityBleBinding.initViewBinding() {
        bluetoothInit.launch(bluetoothPermission)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bluetoothScan.launch(arrayOf(Manifest.permission.BLUETOOTH_SCAN))
        } else {
            startScan()
        }
    }

    override fun BaseViewModel.initViewModel() {

    }

    private fun initBLE() {
        val bluetoothManager = App.instance.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        // 检查设备是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth is not supported.");
            return
        }
        if (mBluetoothAdapter?.isEnabled != true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                bluetoothConnect.launch(arrayOf(Manifest.permission.BLUETOOTH_CONNECT))
            } else {
                connectBluetooth()
            }
        }
        // 获取BLE扫描器
        mBluetoothLeScanner = mBluetoothAdapter?.bluetoothLeScanner
    }

    private fun startScan() {
        val scanFilter = ScanFilter.Builder()
            .build()
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
            == PackageManager.PERMISSION_GRANTED) {
            mBluetoothLeScanner?.startScan(mutableListOf(scanFilter), scanSettings, mScanCallback)
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) ==
            PackageManager.PERMISSION_GRANTED) {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback)
        }
    }

    // 读取BLE设备的服务和特性
    private fun readCharacteristic() {
        val service = mBluetoothGatt?.getService(SERVICE_UUID)
        if (service == null) {
            Log.e(TAG, "Service not found.")
            return
        }
        val characteristic = service.getCharacteristic(CHARACTERISTIC_UUID)
        if (characteristic == null) {
            Log.e(TAG, "Characteristic not found.")
            return
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            == PackageManager.PERMISSION_GRANTED) {
            mBluetoothGatt?.readCharacteristic(characteristic)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) ==
            PackageManager.PERMISSION_GRANTED) {
            mBluetoothLeScanner?.stopScan(mScanCallback)
        }
        mBluetoothGatt?.disconnect()
        mBluetoothGatt?.close()
    }
}