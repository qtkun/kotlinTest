package com.qtk.kotlintest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qtk.kotlintest.domain.command.RequestDayForecastCommand
import com.qtk.kotlintest.domain.model.Forecast
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    val detail = MutableLiveData<Forecast>()

    suspend fun setDetail(id : Long){
        viewModelScope.launch {
            val result = RequestDayForecastCommand(id).execute()
            detail.value = result
        }
    }
}