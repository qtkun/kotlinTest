package com.qtk.kotlintest.view_model

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import com.qtk.kotlintest.base.BaseViewModel
import com.qtk.kotlintest.domain.command.RequestDayForecastCommand
import com.qtk.kotlintest.domain.model.Forecast
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch

class DetailViewModel : BaseViewModel() {
    private val _detail = MutableStateFlow(Forecast(0, 0, "", 0, 0, ""))
    val detail : MutableStateFlow<Forecast> get() = _detail

    val id = MutableStateFlow(0L)

    suspend fun setDetail(id : Long){
        viewModelScope.launch {
            val result = RequestDayForecastCommand(id).execute()
            _detail.value = result
        }
    }

    suspend fun getDetail(lifecycle: Lifecycle, id: Long) =
        RequestDayForecastCommand(id).execute2()
            .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .onStart {
                mutableLoading.postValue(true)
            }.onEach {
                mutableLoading.postValue(false)
            }.onCompletion {
                mutableLoading.postValue(false)
            }.collectLatest {
                _detail.value = it
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