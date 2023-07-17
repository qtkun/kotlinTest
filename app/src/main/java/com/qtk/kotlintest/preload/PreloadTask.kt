package com.qtk.kotlintest.preload

import android.util.Log
import com.danikula.videocache.HttpProxyCacheServer
import java.io.BufferedInputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService

class PreloadTask(
    val mRawUrl: String,
    val mPosition: Int,
    private val mCacheServer: HttpProxyCacheServer
): Runnable {
    companion object {
        private const val TAG = "PreloadTask"
    }

    /**
     * 是否被取消
     */
    private var mIsCanceled: Boolean = false
    /**
     * 是否正在预加载
     */
    private var mIsExecuted: Boolean = false

    override fun run() {
        Log.i(TAG, "mIsCanceled：$mIsCanceled")
        if (!mIsCanceled) {
            start()
        }
        mIsExecuted = false
        mIsCanceled = false
    }

    private fun start() {
        Log.i(TAG, "开始预加载：$mPosition")
        var connection: HttpURLConnection? = null
        var bis: BufferedInputStream? = null
        try {
            val proxyUrl = mCacheServer.getProxyUrl(mRawUrl)
            val url = URL(proxyUrl)
            connection = url.openConnection().apply {
                connectTimeout = 10000
                readTimeout = 10000
            } as? HttpURLConnection
            bis = BufferedInputStream(connection?.inputStream)
            var length: Int
            var read = -1
            val bytes = ByteArray(8 * 1024)
            while (bis.read(bytes).also { length = it } != -1) {
                read += length
                if (mIsCanceled || read >= PreloadManager.PRELOAD_LENGTH) {
                    Log.i(TAG, "结束预加载：$mPosition")
                    break
                }
            }
            if (read == -1) { //这种情况一般是预加载出错了，删掉缓存
                Log.i(TAG, "预加载失败：$mPosition")
                val cacheFile: File = PreloadManager.instance.getCacheFile(mRawUrl)
                if (cacheFile.exists()) {
                    cacheFile.delete()
                }
            } else {
                Log.i(TAG, "结束预加载：$mPosition")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i(TAG,"异常结束预加载：$mPosition|${e.message}\n${mCacheServer.getProxyUrl(mRawUrl)}")
        } finally {
            connection?.disconnect()
            bis?.close()
        }
    }

    fun execute(executorService: ExecutorService) {
        if (mIsExecuted) return
        mIsExecuted = true
        executorService.execute(this)
    }

    /**
     * 取消预加载任务
     */
    fun cancel() {
        if (mIsExecuted) {
            mIsCanceled = true
        }
    }
}