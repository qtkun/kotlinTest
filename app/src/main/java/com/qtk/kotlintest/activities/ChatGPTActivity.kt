package com.qtk.kotlintest.activities

import android.graphics.Rect
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtk.kotlintest.adapter.ChatGPTAdapterProxy
import com.qtk.kotlintest.adapter.MyMessageAdapterProxy
import com.qtk.kotlintest.base.base.BaseActivity
import com.qtk.kotlintest.base.base.MultiAdapter
import com.qtk.kotlintest.databinding.ActivityChatBinding
import com.qtk.kotlintest.extensions.dp
import com.qtk.kotlintest.extensions.hideKeyboard
import com.qtk.kotlintest.extensions.launchOnState
import com.qtk.kotlintest.extensions.singleClick
import com.qtk.kotlintest.view_model.ChatGPTViewModel
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import org.jetbrains.anko.toast

@AndroidEntryPoint
class ChatGPTActivity: BaseActivity<ActivityChatBinding, ChatGPTViewModel>() {
    private val adapter by lazy {
        MultiAdapter(listOf(MyMessageAdapterProxy(), ChatGPTAdapterProxy()))
    }

    private val linearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    private var isKeyboardShow = false

    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)
        val decorHeight = window.decorView.height
        val keyboardHeight = decorHeight - rect.bottom
        if (keyboardHeight > 200 && !isKeyboardShow) {
            if (linearLayoutManager.canScrollVertically() && adapter.lastIndex >= 0) {
                linearLayoutManager.scrollToPositionWithOffset(adapter.lastIndex, 0)
            }
            binding.rvChat.post {
                val lastPosition = linearLayoutManager.findLastVisibleItemPosition()
                val lastView = linearLayoutManager.findViewByPosition(lastPosition)
                val location = IntArray(2)
                lastView?.getLocationOnScreen(location)
                val dy = rect.bottom - location[1] - (lastView?.height ?: 0) - 60.dp
                if (dy < 0f) {
                    binding.rvChat.stopScroll()
                    binding.rvChat.scrollBy(0, -dy)
//                    linearLayoutManager.scrollToPositionWithOffset(adapter.lastIndex, dy)
                }
                isKeyboardShow = true
            }
        } else if (keyboardHeight < 200 && isKeyboardShow) {
            isKeyboardShow = false
        }
    }

    override fun ActivityChatBinding.initViewBinding() {
        rvChat.apply {
            layoutManager = linearLayoutManager
            adapter = this@ChatGPTActivity.adapter
            rvChat.setOnTouchListener { v, event ->
                if (isKeyboardShow && etMessage.hasFocus()) {
                    etMessage.hideKeyboard()
                }
                false
            }
        }
        back.singleClick {
            finish()
        }
        tvSend.singleClick {
            if (etMessage.text.isNotBlank()) {
                val content = etMessage.text.toString()
                adapter.addData(content)
                adapter.notifyItemInserted(adapter.size)
                linearLayoutManager.scrollToPositionWithOffset(adapter.lastIndex, 0)
                viewModel.insertMessage(content)
                viewModel.sendMessageToChatGPT(content)
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
    }

    override fun ChatGPTViewModel.initViewModel() {
        launchOnState(Lifecycle.State.STARTED) {
            message.filter { it != null }
                .collect {
                    adapter.addData(it!!)
                    adapter.notifyItemInserted(adapter.size)
                    linearLayoutManager.scrollToPositionWithOffset(adapter.lastIndex, 0)
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
}