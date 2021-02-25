package com.qtk.kotlintest.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.qtk.kotlintest.App

class TestWork(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        App.instance.loadImage()
        return Result.success()
    }
}