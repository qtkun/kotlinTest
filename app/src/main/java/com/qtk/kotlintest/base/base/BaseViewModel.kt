package com.qtk.kotlintest.base.base

import androidx.lifecycle.ViewModel
import com.qtk.kotlintest.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.jetbrains.anko.toast

open class BaseViewModel: ViewModel(){
    private val mutableLoading = MutableStateFlow(false)
    val loading: StateFlow<Boolean>
        get() = mutableLoading

    protected fun<T> Flow<T>.baseLoading(): Flow<T> {
        return flowOn(Dispatchers.IO).onStart {
            mutableLoading.value = true
        }.catch {
            App.instance.toast(it.message ?: "数据异常")
            mutableLoading.value = false
            it.printStackTrace()
        }.onCompletion {
            mutableLoading.value = false
        }
    }

    protected fun<T> Flow<T>.base(): Flow<T> {
        return flowOn(Dispatchers.IO).catch {
            App.instance.toast(it.message ?: "数据异常")
            it.printStackTrace()
        }
    }
}
