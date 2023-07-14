package com.qtk.kotlintest.view_model

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import com.qtk.kotlintest.base.BaseViewModel
import com.qtk.kotlintest.domain.command.RequestDayForecastCommand
import com.qtk.kotlintest.domain.model.Forecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class DetailViewModel @Inject constructor() : BaseViewModel() {
    private val _detail = MutableStateFlow(Forecast(0, 0, "", 0, 0, ""))
    val detail : MutableStateFlow<Forecast> get() = _detail

    val id = MutableStateFlow(0L)
    private val countdownLatch = CountDownLatch(2)

    fun test () = viewModelScope.launch {
        val d1 = getDetail(id.value)
        val d2 = getDetail(id.value)
        Log.i("QTK", d1.description)
        Log.i("QTK", d2.description)
    }

    suspend fun setDetail(id : Long): Forecast{
        val result = RequestDayForecastCommand(id).execute()
        countdownLatch.countDown()
        return result
    }

    suspend fun getDetail(id: Long) = suspendCoroutine<Forecast> { cont ->
        viewModelScope.launch {
            RequestDayForecastCommand(id).execute2()
                .catch {
                    cont.resumeWithException(it)
                }
                .onStart {
                    mutableLoading.postValue(true)
                }.onEach {
                    mutableLoading.postValue(false)
                }.onCompletion {
                    mutableLoading.postValue(false)
                }.collectLatest {
                    cont.resume(it)
                }
        }
    }

    val detail2: StateFlow<Forecast> = id.filter { it != 0L }.flatMapLatest {
        RequestDayForecastCommand(it).execute2()
            .onStart {
                mutableLoading.postValue(true)
            }.onEach {
                mutableLoading.postValue(false)
            }.onCompletion {
                mutableLoading.postValue(false)
            }
    }.stateIn(viewModelScope, Lazily, Forecast(0, 0, "", 0, 0, ""))
}