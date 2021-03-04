package com.qtk.kotlintest.work

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.qtk.kotlintest.App
import com.qtk.kotlintest.contant.BITMAP_ID
import com.qtk.kotlintest.extensions.drawable
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class SaveImageWorker(context: Context, workerParameters: WorkerParameters)
    : Worker(context, workerParameters) {
    override fun doWork(): Result {
        val option: BitmapFactory.Options = BitmapFactory.Options()
        val bitmap: Bitmap? = inputData.getInt(BITMAP_ID, 0).let {
            BitmapFactory.decodeResource(applicationContext.resources, it, option)
        }
        return if (bitmap != null) {
            App.instance.addBitmapToAlbum(bitmap, "bg.png",
                option.outMimeType, Bitmap.CompressFormat.PNG)
            Result.success()
        } else {
            Result.failure()
        }
    }
}