package com.qtk.kotlintest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager

class MuteHelper(private val context: Context) {
    companion object {
        private const val ACTION = "android.media.STREAM_MUTE_CHANGED_ACTION"
    }

    private var muteListener: MuteListener? = null

    private var mMuteReceiver: MuteReceiver? = null

    interface MuteListener {
        fun onMuteChange(mute: Boolean)
    }

    fun registerMuteListener(listener: MuteListener) {
        muteListener = listener
        mMuteReceiver = MuteReceiver()
        context.registerReceiver(mMuteReceiver, IntentFilter(ACTION))
    }

    fun unregisterMuteListener() {
        mMuteReceiver?.let {
            context.unregisterReceiver(it)
            mMuteReceiver = null
        }
        muteListener = null
    }

    inner class MuteReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.run {
                if(action == ACTION) {
                    (context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager)?.let {
                        muteListener?.onMuteChange(it.isStreamMute(AudioManager.STREAM_MUSIC))
                    }
                }
            }
        }
    }
}