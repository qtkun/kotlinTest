package com.qtk.kotlintest.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.StyleRes
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.Lifecycle
import com.blankj.utilcode.util.ActivityUtils
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.DialogNoticeBinding
import com.qtk.kotlintest.extensions.viewBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class NoticeDialog @JvmOverloads constructor(
    context: Context,
    @StyleRes themeResId: Int = R.style.NoticeDialog
): AppCompatDialog(context, themeResId) {
    private val binding by viewBinding<DialogNoticeBinding>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + CoroutineName("NoticeDialog"))
    private var dismissJob: Job? = null
    private var downY: Float = 0f
    private var moveY: Float = 0f
    private var isSwipeToDismiss = false

    private var onDismissCallBack: OnDismissCallBack? = null

    fun setOnDismissCallBack(onDismissCallBack: OnDismissCallBack?): NoticeDialog {
        this.onDismissCallBack = onDismissCallBack
        return this
    }

    init {
        binding.root.setOnTouchListener { _, event ->
            handleTouchEvent(event)
            true
        }
        binding.root.setOnClickListener {
            dismiss()
        }
        window?.run {
            setDimAmount(0f)
            setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            /*setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)*/
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            attributes.windowAnimations = R.style.NoticeDialogEnter
            setGravity(Gravity.TOP)
        }
        setOnDismissListener {
            if (dismissJob?.isActive == true) {
                dismissJob?.cancel()
                dismissJob = null
            }
            NoticeManager.instance.dismiss()
            onDismissCallBack?.invoke()
        }
    }

    private fun handleTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                moveY = event.rawY
                isSwipeToDismiss = (moveY - downY) < 0f
            }
            MotionEvent.ACTION_UP -> {
                if (isSwipeToDismiss && abs(moveY - downY) > 100f) {
                    dismiss()
                }
            }
        }
        return true
    }

    fun show(text: String) {
        binding.tvNotice.text = text
        show()
        dismissJob = scope.launch(Dispatchers.Default) {
            (5 downTo 1).asFlow()
                .collect {
                    delay(1000L)
                }
            dismiss()
        }
    }

    fun interface OnDismissCallBack: () -> Unit
}

class NoticeManager {
    companion object {
        private const val TAG = "NoticeManager"
        val instance by lazy {
            NoticeManager()
        }
    }
    private val noticeQueue = Channel<String>(Channel.UNLIMITED)
    private var isShowing: Boolean = false
    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("NoticeManager"))

    init {
        loop()
    }

    fun addNotice(notice: String) {
        scope.launch {
            noticeQueue.send(notice)
        }
    }

    private fun loop() {
        scope.launch {
            while (true) {
                Log.i(TAG, "loop")
                val notice = noticeQueue.receive()
                (ActivityUtils.getTopActivity() as? ComponentActivity)?.let { activity ->
                    mutex.withLock {
                        if (!isShowing && activity.lifecycle.currentState > Lifecycle.State.STARTED) {
                            isShowing = true
                            showNotice(activity, notice)
                        }
                    }
                }
                delay(1000L)
            }
        }
    }

    private fun showNotice(context: Context, notice: String) {
        scope.launch(Dispatchers.Main) {
            NoticeDialog(context).show(notice)
        }
    }

    fun dismiss() {
        scope.launch {
            mutex.withLock {
                isShowing = false
            }
        }
    }
}