package com.qtk.kotlintest.activities

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewTreeObserver
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtk.flowbus.observe.observeEvent
import com.qtk.kotlintest.adapter.ChatGPTAdapterProxy
import com.qtk.kotlintest.adapter.MyMessageAdapterProxy
import com.qtk.kotlintest.base.base.BaseActivity
import com.qtk.kotlintest.base.base.MultiAdapter
import com.qtk.kotlintest.databinding.ActivityChatBinding
import com.qtk.kotlintest.extensions.hideKeyboard
import com.qtk.kotlintest.extensions.launchOnState
import com.qtk.kotlintest.extensions.singleClick
import com.qtk.kotlintest.room.entity.ChatMessageBean
import com.qtk.kotlintest.room.entity.UserMessageBean
import com.qtk.kotlintest.view_model.ChatGPTViewModel
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import org.jetbrains.anko.toast
import kotlin.math.abs

@AndroidEntryPoint
class ChatGPTActivity: BaseActivity<ActivityChatBinding, ChatGPTViewModel>() {
    private val adapter by lazy {
        MultiAdapter(listOf(MyMessageAdapterProxy(this::deleteMessage), ChatGPTAdapterProxy(this::deleteMessage)))
    }

    private val linearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    private var isKeyboardShow = false
    private val visibleRect = Rect()
    private val scrollRunnable = ScrollRunnable()

    private val mHandler = Handler(Looper.getMainLooper())

    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        window.decorView.getWindowVisibleDisplayFrame(visibleRect)
        val decorHeight = window.decorView.height
        val keyboardHeight = decorHeight - visibleRect.bottom
        if (keyboardHeight > 200 && !isKeyboardShow) {
            val lastPosition = linearLayoutManager.findLastVisibleItemPosition()
            if (adapter.lastIndex >= 0 && lastPosition < adapter.lastIndex) {
                linearLayoutManager.scrollToPositionWithOffset(adapter.lastIndex, 0)
            }
            mHandler.post(scrollRunnable)
            isKeyboardShow = true
        } else if (keyboardHeight < 200 && isKeyboardShow) {
            isKeyboardShow = false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun ActivityChatBinding.initViewBinding() {
        rvChat.apply {
            layoutManager = linearLayoutManager
            adapter = this@ChatGPTActivity.adapter
            rvChat.setOnTouchListener { _, _ ->
                if (isKeyboardShow && etMessage.hasFocus()) {
                    etMessage.hideKeyboard()
                }
                false
            }
        }
        back.singleClick {
            finish()
        }
        tvDeleteAll.singleClick {
            viewModel.deleteAllMessages()
            adapter.removeAll()
        }
        tvSend.singleClick {
            if (etMessage.text.isNotBlank()) {
                val content = etMessage.text.toString()
                viewModel.saveUserMessage(content)
                etMessage.setText("")
            } else {
                toast("请输入内容")
            }
        }
        UltimateBarX.addStatusBarTopPadding(titleBar)
        window.decorView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        mHandler.removeCallbacks(scrollRunnable)
    }

    override fun ChatGPTViewModel.initViewModel() {
        observeEvent<String>("platform") {
            toast(it)
        }
        launchOnState(Lifecycle.State.STARTED) {
            message.filter { it != null }
                .collect {
                    adapter.addData(it!!)
                    linearLayoutManager.scrollToPositionWithOffset(adapter.lastIndex, 0)
                    if (it is UserMessageBean) {
                        viewModel.sendMessageToChatGPT(adapter.data)
                    }
                    message.value = null
                }
        }
        launchOnState(Lifecycle.State.STARTED) {
            historyMessage.filter { it.isNotEmpty() }
                .collect {
                    adapter.setData(it)
                    linearLayoutManager.scrollToPositionWithOffset(adapter.lastIndex, 0)
                }
        }
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        super.initVariables(savedInstanceState)
        viewModel.getMessageFromRoom()
    }

    private fun deleteMessage(position: Int) {
        adapter[position].let { item ->
            when (item) {
                is UserMessageBean -> viewModel.deleteMessage(item.mapToChatMessage())
                is ChatMessageBean -> viewModel.deleteMessage(item)
                else -> Unit
            }
        }
        adapter.removeAt(position)
    }

    inner class ScrollRunnable: Runnable {
        override fun run() {
            val lastPosition = linearLayoutManager.findLastVisibleItemPosition()
            val lastView = linearLayoutManager.findViewByPosition(lastPosition) ?: return
            val location = IntArray(2)
            lastView.getLocationOnScreen(location)
            val bottomHeight = binding.llBottom.height
            val scrollDy = visibleRect.bottom - bottomHeight - location[1] - lastView.height
            if (scrollDy < 0f) {
                binding.rvChat.stopScroll()
                val offset = lastView.top - abs(scrollDy)
                linearLayoutManager.scrollToPositionWithOffset(adapter.lastIndex, offset)
            }
        }
    }
}