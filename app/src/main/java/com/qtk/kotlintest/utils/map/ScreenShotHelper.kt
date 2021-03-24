package com.qtk.kotlintest.utils.map

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import com.amap.api.maps.MapView
import org.jetbrains.anko.toast


/**
 * 组装地图截图和其他View截图，并且将截图存储在本地sdcard，需要注意的是目前提供的方法限定为MapView与其他View在同一个ViewGroup下
 * @param    bitmap             地图截图回调返回的结果
 * @param   viewContainer      MapView和其他要截图的View所在的父容器ViewGroup
 * @param   mapView            MapView控件
 * @param   views              其他想要在截图中显示的控件
 */
fun saveScreenShot(
    context: Context,
    bitmap: Bitmap,
    viewContainer: ViewGroup,
    mapView: MapView,
    vararg views: View?
) {
    val screenShotBitmap =
        getMapAndViewScreenShot(bitmap, viewContainer, mapView, *views)
    val values = ContentValues()
    val displayName = "截图_${System.currentTimeMillis()}.png"
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
    values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
    } else {
        values.put(
            MediaStore.MediaColumns.DATA,
            "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName"
        )
    }
    context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        ?.let { uri ->
            context.contentResolver.openOutputStream(uri).use {
                it?.let {
                    if (screenShotBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) {
                        context.toast("截图成功")
                    }
                }
            }
        }
}

/**
 * 组装地图截图和其他View截图，需要注意的是目前提供的方法限定为MapView与其他View在同一个ViewGroup下
 * @param    bitmap             地图截图回调返回的结果
 * @param   viewContainer      MapView和其他要截图的View所在的父容器ViewGroup
 * @param   mapView            MapView控件
 * @param   views              其他想要在截图中显示的控件
 */
private fun getMapAndViewScreenShot(
    bitmap: Bitmap,
    viewContainer: ViewGroup,
    mapView: MapView,
    vararg views: View?
): Bitmap {
    val screenBitmap = Bitmap.createBitmap(viewContainer.width, viewContainer.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(screenBitmap)
    canvas.drawBitmap(bitmap, mapView.left.toFloat(), mapView.top.toFloat(), null)
    for (view in views) {
        view?.let {
            canvas.drawBitmap(it.drawToBitmap(), it.left.toFloat(), it.top.toFloat(), null)
        }
    }
    return screenBitmap
}