package com.qtk.kotlintest.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.qtk.kotlintest.App

class TestWork(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        App.instance.loadImage().let {
            App.instance.copyUriToExternalFilesDir(it[0], App.instance.getUriName(it[0]))
        }
        return Result.success()
    }
}