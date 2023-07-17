package com.qtk.kotlintest.view_model

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.qtk.kotlintest.base.BaseViewModel
import com.qtk.kotlintest.domain.command.RequestDayForecastCommand
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.extensions.collectAwait
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.concurrent.CountDownLatch

@HiltViewModel
class DetailViewModel @Inject constructor() : BaseViewModel() {
    private val _detail = MutableStateFlow(Forecast(0, 0, "", 0, 0, ""))
    val detail : MutableStateFlow<Forecast> get() = _detail

    val id = MutableStateFlow(0L)
    private val countdownLatch = CountDownLatch(2)

    fun test () = viewModelScope.launch {
        val d1 = getDetail(id.value)
        val d2 = getDetail(id.value.plus(1))
        Log.i("DetailViewModel", d1.toString())
        Log.i("DetailViewModel", d2.toString())
    }

    suspend fun setDetail(id : Long): Forecast{
        val result = RequestDayForecastCommand(id).execute()
        countdownLatch.countDown()
        return result
    }

    private suspend fun getDetail(id: Long) =  RequestDayForecastCommand(id).execute2()
        .onStart {
            mutableLoading.postValue(true)
        }.onEach {
            mutableLoading.postValue(false)
        }.onCompletion {
            mutableLoading.postValue(false)
        }.collectAwait(viewModelScope)

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