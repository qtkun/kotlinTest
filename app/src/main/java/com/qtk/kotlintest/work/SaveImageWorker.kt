package com.qtk.kotlintest.work

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.*
import com.qtk.kotlintest.App
import com.qtk.kotlintest.R
import com.qtk.kotlintest.contant.BITMAP_ID
import com.qtk.kotlintest.extensions.drawable
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class SaveImageWorker(context: Context, workerParameters: WorkerParameters)
    : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())
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

    private fun createForegroundInfo(): ForegroundInfo {
        //每一个 Notification 需要使用不同的 id
        val notificationId = 1
        return ForegroundInfo(notificationId, createNotification())
    }

    private fun createNotification(): Notification {
        //PendingIntent 可用来取消 Worker
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
        val channelId = "saveImage"
        val title = "保存图片"
        val action = Notification.Action.Builder(
            Icon.createWithResource(applicationContext, R.drawable.ic_cancel),
            "取消", intent
        ).build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Notification.Builder(applicationContext, channelId)
                .setContentTitle(title)
                .setTicker(title)
                .setSmallIcon(R.drawable.ic_baseline_gradient)
                .setOngoing(true)
                .addAction(action)
                .setChannelId(createNotificationChannel(channelId, title).id)
                .build()
        } else {
            return Notification.Builder(applicationContext)
                .setContentTitle(title)
                .setTicker(title)
                .setSmallIcon(R.drawable.ic_baseline_gradient)
                .setOngoing(true)
                .addAction(action)
                .build()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        name: String
    ): NotificationChannel {
        return NotificationChannel(
            channelId, name, NotificationManager.IMPORTANCE_LOW
        ).also { channel ->
            val notificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}