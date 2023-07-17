package com.qtk.kotlintest.preload

import android.content.Context
import android.util.Log
import com.danikula.videocache.StorageUtils
import com.danikula.videocache.file.Md5FileNameGenerator
import com.qtk.kotlintest.App
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager
import java.io.File
import java.util.concurrent.Executors

class PreloadManager (private val context: Context) {
    companion object {
        private const val TAG = "PreloadManager"
        val instance:  PreloadManager by lazy {
            PreloadManager(App.instance)
        }

        /**
         * 预加载的大小
         */
        const val PRELOAD_LENGTH = 512 * 1024
    }

    private var mIsStartPreload = true

    private val mHttpProxyCacheServer = ProxyCacheManager.getProxy(context, null)

    private val mExecutorService = Executors.newFixedThreadPool(4)

    private val mPreloadTasks = LinkedHashMap<String, PreloadTask>()

    private val md5FileNameGenerator = Md5FileNameGenerator()

    /**
     * 删除缓存文件
     *
     * @param url
     * @return
     */
    fun deleteCacheFile(url: String): Boolean {
        try {
            val file = getCacheFile(url)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    /**
     * 开始预加载
     *
     * @param rawUrl 原始视频地址
     */
    fun addPreloadTask(rawUrl: String, position: Int) {
        if (isPreloaded(rawUrl)) return
        val task = PreloadTask(rawUrl, position, mHttpProxyCacheServer)
        Log.i(TAG, "addPreloadTask: $position")
        mPreloadTasks[rawUrl] = task
        if (mIsStartPreload) {
            //开始预加载
            task.execute(mExecutorService)
        }
    }

    /**
     * 判断该播放地址是否已经预加载
     */
    private fun isPreloaded(rawUrl: String): Boolean {
        //先判断是否有缓存文件，如果已经存在缓存文件，并且其大小大于1KB，则表示已经预加载完成了
        val cacheFile = getCacheFile(rawUrl)
        if (cacheFile.exists()) {
            return if (cacheFile.length() >= 1024) {
                true
            } else {
                //这种情况一般是缓存出错，把缓存删掉，重新缓存
                cacheFile.delete()
                false
            }
        }
        //再判断是否有临时缓存文件，如果已经存在临时缓存文件，并且临时缓存文件超过了预加载大小，则表示已经预加载完成了
        val tempCacheFile: File = getTempCacheFile(rawUrl)
        if (tempCacheFile.exists()) {
            return tempCacheFile.length() >= PRELOAD_LENGTH
        }
        return false
    }

    /**
     * 暂停预加载
     * 根据是否反向滑动取消在position之下或之上的PreloadTask
     *
     * @param position        当前滑到的位置
     * @param isReverseScroll 列表是否反向滑动
     */
    fun pausePreload(position: Int, isReverseScroll: Boolean) {
        Log.d(TAG, "pausePreload：$position isReverseScroll: $isReverseScroll")
        mIsStartPreload = false
        for ((_, task) in mPreloadTasks) {
            if (isReverseScroll) {
                if (task.mPosition >= position) {
                    task.cancel()
                }
            } else {
                if (task.mPosition <= position) {
                    task.cancel()
                }
            }
        }
    }

    /**
     * 恢复预加载
     * 根据是否反向滑动开始在position之下或之上的PreloadTask
     *
     * @param position        当前滑到的位置
     * @param isReverseScroll 列表是否反向滑动
     */
    fun resumePreload(position: Int, isReverseScroll: Boolean) {
        Log.d(TAG, "resumePreload：$position isReverseScroll: $isReverseScroll")
        mIsStartPreload = true
        for ((_, task) in mPreloadTasks) {
            if (isReverseScroll) {
                if (task.mPosition < position && !isPreloaded(task.mRawUrl)) {
                    task.execute(mExecutorService)
                }
            } else {
                if (task.mPosition > position && !isPreloaded(task.mRawUrl)) {
                    task.execute(mExecutorService)
                }
            }
        }
    }

    /**
     * 继续执行预加载,边下载边播放
     */
    fun continuePreLoad(position: Int, isReverseScroll: Boolean) {
        Log.d(TAG, "resumePreload：$position isReverseScroll: $isReverseScroll")
        mIsStartPreload = true
        for ((_, task) in mPreloadTasks) {
            if (isReverseScroll) {
                if (task.mPosition < position && !isPreloaded(task.mRawUrl)) {
                    task.execute(mExecutorService)
                }
            } else {
                if (task.mPosition > position && !isPreloaded(task.mRawUrl)) {
                    task.execute(mExecutorService)
                }
            }
        }
    }

    /**
     * 通过原始地址取消预加载
     *
     * @param rawUrl 原始地址
     */
    fun removePreloadTask(rawUrl: String) {
        val task = mPreloadTasks[rawUrl]
        if (task != null) {
            task.cancel()
            mPreloadTasks.remove(rawUrl)
        }
    }

    /**
     * 取消所有的预加载
     */
    fun removeAllPreloadTask() {
        val iterator = mPreloadTasks.entries.iterator()
        while (iterator.hasNext()) {
            val (_, task) = iterator.next()
            task.cancel()
            iterator.remove()
        }
    }

    /**
     * 停止所有的预缓存任务
     */
    fun stopAllPreloadTask() {
        val iterator = mPreloadTasks.entries.iterator()
        while (iterator.hasNext()) {
            val (_, task) = iterator.next()
            task.cancel()
        }
    }

    /**
     * 获取播放地址
     */
    fun getPlayUrl(rawUrl: String): String {
        val task = mPreloadTasks[rawUrl]
        task?.cancel()
        return if (isPreloaded(rawUrl)) {
            mHttpProxyCacheServer.getProxyUrl(rawUrl)
        } else {
            rawUrl
        }
    }

    fun getCacheFile(url: String): File {
        val name = md5FileNameGenerator.generate(url)
        return File(StorageUtils.getIndividualCacheDirectory(context), name)
    }

    private fun getTempCacheFile(url: String): File {
        val name = "${md5FileNameGenerator.generate(url)}.download"
        return File(StorageUtils.getIndividualCacheDirectory(context), name)
    }
}