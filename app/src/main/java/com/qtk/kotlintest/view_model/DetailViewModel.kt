package com.qtk.kotlintest.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qtk.kotlintest.base.BaseViewModel
import com.qtk.kotlintest.domain.command.RequestDayForecastCommand
import com.qtk.kotlintest.domain.model.Forecast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DetailViewModel : BaseViewModel() {
    private val _detail = MutableLiveData<Forecast>()
    val detail : LiveData<Forecast> get() = _detail

    suspend fun setDetail(id : Long){
        viewModelScope.launch {
            val result = RequestDayForecastCommand(id).execute()
            _detail.value = result
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun getDetail(id: Long) =
        RequestDayForecastCommand(id).execute2()
            .onStart {
                mutableLoading.postValue(true)
            }.onEach {
                mutableLoading.postValue(false)
            }.collectLatest {
                mutableLoading.postValue(false)
                _detail.postValue(it)
            }
}