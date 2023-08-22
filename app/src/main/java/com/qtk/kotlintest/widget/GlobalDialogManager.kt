package com.qtk.kotlintest.widget

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import com.blankj.utilcode.util.ActivityUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GlobalDialogManager {
    companion object {
        val instance by lazy {
            GlobalDialogManager()
        }
    }
    private val dialogQueue = Channel<GlobalDialogBean>(UNLIMITED)
    private var isShowing: Boolean = false
    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("GlobalDialogManager"))

    init {
        loop()
    }

    fun sendDialogRequest(dialogBean: GlobalDialogBean) {
        scope.launch {
            dialogQueue.send(dialogBean)
        }
    }

    private fun loop() {
        scope.launch {
            while (true) {
                (ActivityUtils.getTopActivity() as? ComponentActivity)?.let { activity ->
                    mutex.withLock {
                        if (!isShowing && activity.lifecycle.currentState > Lifecycle.State.STARTED) {
                            val dialogBean = dialogQueue.receive()
                            getDialog(activity, dialogBean) ?.let { dialog ->
                                if (dialog.handleDialog(dialogBean)) {
                                    isShowing = true
                                    showDialog(dialogBean, dialog)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getDialog(context: Context, dialogBean: GlobalDialogBean): IGlobalDialog? {
        val dialog: IGlobalDialog? = null
        when(dialogBean.type) {

        }
        return dialog
    }

    private fun showDialog(dialogBean: GlobalDialogBean, dialog: IGlobalDialog) {
        scope.launch(Dispatchers.Main) {
            dialog.showDialog(dialogBean)
        }
    }

    fun dismissDialog() {
        scope.launch {
            mutex.withLock {
                isShowing = false
            }
        }
    }
}

data class GlobalDialogBean(
    val type: String,
)

interface IGlobalDialog {
    fun showDialog(globalDialogBean: GlobalDialogBean)
    fun handleDialog(globalDialogBean: GlobalDialogBean): Boolean
}
