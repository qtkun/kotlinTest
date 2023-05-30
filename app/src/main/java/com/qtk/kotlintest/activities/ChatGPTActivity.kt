package com.qtk.kotlintest.activities

import android.annotation.SuppressLint
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
import com.qtk.kotlintest.extensions.hideKeyboard
import com.qtk.kotlintest.extensions.launchOnState
import com.qtk.kotlintest.extensions.singleClick
import com.qtk.kotlintest.room.entity.ChatMessageBean
import com.qtk.kotlintest.room.entity.Role
import com.qtk.kotlintest.room.entity.UserMessageBean
import com.qtk.kotlintest.view_model.ChatGPTViewModel
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import org.jetbrains.anko.toast
import java.util.UUID
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

    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)
        val decorHeight = window.decorView.height
        val keyboardHeight = decorHeight - rect.bottom
        if (keyboardHeight > 200 && !isKeyboardShow) {
            var lastPosition = linearLayoutManager.findLastVisibleItemPosition()
            if (adapter.lastIndex >= 0 && lastPosition < adapter.lastIndex) {
                linearLayoutManager.scrollToPositionWithOffset(adapter.lastIndex, 0)
            }
            binding.rvChat.post {
                lastPosition = linearLayoutManager.findLastVisibleItemPosition()
                val lastView = linearLayoutManager.findViewByPosition(lastPosition)
                val location = IntArray(2)
                lastView?.getLocationOnScreen(location)
                val bottomHeight = binding.llBottom.height
                val scrollDy = rect.bottom - bottomHeight - location[1] - (lastView?.height ?: 0)
                if (scrollDy < 0f) {
                    binding.rvChat.stopScroll()
                    val offset = (lastView?.top ?: 0) - abs(scrollDy)
                    linearLayoutManager.scrollToPositionWithOffset(adapter.lastIndex, offset)
                }
            }
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
                val message = UserMessageBean(UUID.randomUUID().toString(), content, Role.USER)
                viewModel.message.value = message
                viewModel.insertMessage(message.mapToChatMessage())
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
}