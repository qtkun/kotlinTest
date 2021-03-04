package com.qtk.kotlintest

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.qtk.kotlintest.domain.data.room.AppDatabase
import com.qtk.kotlintest.extensions.DelegatesExt
import com.qtk.kotlintest.method.ToastMethod
import com.qtk.kotlintest.modules.appModule
import com.qtk.kotlintest.modules.viewModelModule
import dagger.hilt.android.HiltAndroidApp
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.io.*
import kotlin.concurrent.thread

/**
 * Created by qtkun
on 2020-06-16.
 */
@HiltAndroidApp
class App : Application() {
    companion object {
        var instance: App by DelegatesExt.notNullSingleValue()
    }

    lateinit var fE1: FlutterEngine
    lateinit var fE2: FlutterEngine
    lateinit var fE3: FlutterEngine

//    val catalogue = getExternalFilesDir("file")

    override fun onCreate() {
        super.onCreate()
        instance = this
        initFE()
        ToastMethod.registerWith(this)
        startKoin {
            androidLogger()
            androidContext(this@App)
            androidFileProperties()
            modules(listOf(viewModelModule))
            modules(listOf(appModule))
        }
    }

    private fun initFE() {
        fE1 = initEngine("route?{\"desc\":\"点击按钮\"}", "test")
        fE2 = initEngine("route2", "test2")
        fE3 = initEngine("route3", "test3")
    }

    private fun initEngine(route: String, engineId: String): FlutterEngine {
        val fE = FlutterEngine(this)
        //可设置初始路由
        fE.navigationChannel.setInitialRoute(route)
        fE.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
        FlutterEngineCache.getInstance().put(engineId, fE)
        return fE
    }

    override fun onTerminate() {
        AppDatabase.destroyInstance()
        super.onTerminate()
    }

    //读取图片文件
    suspend fun loadImage(): ArrayList<Uri> = withContext(Dispatchers.IO) {
        arrayListOf<Uri>().apply {
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                "${MediaStore.MediaColumns.DATE_ADDED} desc"
            )?.use {
                while (it.moveToNext()) {
                    val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    add(uri)
                    println("image uri is $uri")
                }
            }
        }
    }

    fun getUriName(uri: Uri): String {
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        contentResolver.query(uri, projection, null, null, null).use {
            it?.let {
                if(it.moveToFirst()) {
                    return it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                }
            }
        }
        return ""
    }

    //添加图片到相册
    fun writeInputStreamToAlbum(inputStream: InputStream, displayName: String, mimeType: String) {
        thread {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            } else {
                values.put(
                    MediaStore.MediaColumns.DATA,
                    "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName"
                )
            }
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?.let { uri ->
                    inputStream.use {
                        contentResolver.openOutputStream(uri)?.sink()?.buffer()
                            ?.write(it.source().buffer().readByteArray())?.close()
                    }
                }
        }
    }

    //添加图片到相册
    fun addBitmapToAlbum(
        bitmap: Bitmap,
        displayName: String,
        mimeType: String,
        compressFormat: Bitmap.CompressFormat
    ) {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        } else {
            values.put(
                MediaStore.MediaColumns.DATA,
                "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName"
            )
        }
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?.let { uri ->
                contentResolver.openOutputStream(uri).use {
                    it?.let {
                        bitmap.compress(compressFormat, 100, it)
                    }
                }
            }
    }

    //复制文件到应用程序的关联目录并返回绝对路径
    fun copyUriToExternalFilesDir(uri: Uri, fileName: String): String? {
        val inputStream = contentResolver.openInputStream(uri)
        val tempDir = getExternalFilesDir("temp")
        if (inputStream != null && tempDir != null) {
            val file = File("$tempDir/$fileName")
            inputStream.use {
                file.sink().buffer().write(it.source().buffer().readByteArray()).close()
            }
            println(file.absolutePath)
            return file.absolutePath
        }
        return null
    }
}