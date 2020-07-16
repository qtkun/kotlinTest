package com.qtk.kotlintest

import androidx.lifecycle.*
import com.qtk.kotlintest.domain.command.RequestForecastCommand
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.domain.model.ForecastList
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val data = MutableLiveData<ForecastList>()

    suspend fun setData(zipCode : Long) {
        //协程对viewModel
        viewModelScope.launch {
            val result = RequestForecastCommand(zipCode).execute()
            data.value = result
            print(data.value.toString())
        }
    }

    fun getItem(position : Int) : Forecast {
        return data.value!![position]
    }
}