package com.qtk.kotlintest.activities

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.BaseActivity
import com.qtk.kotlintest.contant.locationPermission
import com.qtk.kotlintest.databinding.ActivityMapBinding
import com.qtk.kotlintest.extensions.asDp
import com.qtk.kotlintest.extensions.toast
import com.qtk.kotlintest.room.PokemonDao
import com.qtk.kotlintest.utils.map.PathSmoothTool
import com.qtk.kotlintest.utils.map.saveScreenShot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MapActivity: BaseActivity<ActivityMapBinding>(R.layout.activity_map) {
    private val aMap by lazy { binding.map.map }

    private val myLocationStyle by lazy {
        MyLocationStyle().apply {
            interval(2000)
            myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
            myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.photo))
        }
    }

    private val backgroundLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        it?.let {
            toast(it.toString())
        }
    }

    private val coarseLocation = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
            it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            aMap.isMyLocationEnabled = true// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            /*mPointList.addAll(TraceAsset.parseLocationsData(
                this.assets,
                "traceRecord" + File.separator + "AMapTrace2.csv"
            ))
            addLocPath()*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                backgroundLocation.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    private val mPointList = arrayListOf<LatLng>()
    private var firstPoint: LatLng? = null
    private var mKalmanPolyline: Polyline? = null
    private var mOriginPolyline: Polyline? = null
    private val mPathSmoothTool: PathSmoothTool by lazy {
        PathSmoothTool().apply {
            intensity = 4
        }
    }

    @Inject
    lateinit var pokemonDao: PokemonDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.map.onCreate(savedInstanceState)
        binding.presenter = MapPresenter()
        initMap()
        coarseLocation.launch(locationPermission)
        lifecycleScope.launch {
            /*pokemonDao.getLocations(getYesterdayMillis(), getTodayMillis())?.also {
                println("定位点数量：${it.size}")
                for(location in it) {
                    mPointList.add(LatLng(location.latitude, location.longitude))
                }
            }
            addLocPath()*/
            /*pokemonDao.getAllLocations()?.also {
                println("定位点数量：${it.size}")
                for(location in it) {
                    mPointList.add(LatLng(location.latitude, location.longitude))
                }
            }*/
        }
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.map.onSaveInstanceState(outState)
    }

    private fun initMap() {
        /**
         * 设置一些amap的属性
         */
        with(aMap.uiSettings) {
            isCompassEnabled = false// 设置指南针是否显示
            isZoomControlsEnabled = false// 设置缩放按钮是否显示
//            zoomPosition = AMapOptions.ZOOM_POSITION_RIGHT_CENTER
            isScaleControlsEnabled = true// 设置比例尺是否显示
            isRotateGesturesEnabled = false// 设置地图旋转是否可用
            isTiltGesturesEnabled = false// 设置地图倾斜是否可用
            isMyLocationButtonEnabled = true// 设置默认定位按钮是否显示
            isIndoorSwitchEnabled = true
        }

        /** 自定义系统定位小蓝点
         *
         */
        with(aMap) {
            myLocationStyle = this@MapActivity.myLocationStyle
            addOnMyLocationChangeListener {
                /*if (firstPoint == null) {
                    firstPoint = LatLng(it.latitude, it.longitude)
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(firstPoint))
                }*/
            }
            showIndoorMap(true)
            moveCamera(CameraUpdateFactory.zoomTo(15f))
            isTouchPoiEnable = true
        }
    }

    //在地图上添加本地轨迹数据，并处理
    private fun addLocPath() {
        if (mPointList.size > 0) {
            mOriginPolyline = aMap.addPolyline(PolylineOptions().addAll(mPointList).color(Color.GREEN))
                ?.apply { isVisible = true }
            aMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    getBounds(pathOptimize(mPointList)),
                    30.asDp().toInt()
                )
            )
        }
    }

    //轨迹平滑优化
    private fun pathOptimize(originList: List<LatLng>): List<LatLng>? {
        val pathOptimizeList: List<LatLng>? = mPathSmoothTool.pathOptimize(originList)
        mKalmanPolyline = aMap.addPolyline(
            PolylineOptions().addAll(pathOptimizeList).color(Color.parseColor("#FFC125"))
        )?.apply { isVisible = true }
        return pathOptimizeList
    }

    private fun getBounds(pointList: List<LatLng>?): LatLngBounds? {
        val b = LatLngBounds.builder()
        if (pointList == null) {
            return b.build()
        }
        for (i in pointList.indices) {
            b.include(pointList[i])
        }
        return b.build()
    }

    inner class MapPresenter {
        fun screenShot() {
            aMap.getMapScreenShot(object : AMap.OnMapScreenShotListener{
                override fun onMapScreenShot(p0: Bitmap?) {
                    p0?.let {
                        saveScreenShot(this@MapActivity, it, binding.mapLayout, binding.map, binding.screenShot)
                    }
                }

                override fun onMapScreenShot(p0: Bitmap?, p1: Int) {}
            })
        }
    }
}