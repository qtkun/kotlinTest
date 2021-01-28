package com.qtk.kotlintest.view_model

import android.app.Dialog
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.qtk.kotlintest.domain.command.RequestForecastCommand
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.domain.model.ForecastList
import com.qtk.kotlintest.extensions.toJson
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(val moshi: Moshi) : ViewModel() {
    private val data = MutableLiveData<ForecastList>()
    val forecastList : LiveData<ForecastList> = data
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    suspend fun setData(zipCode : Long) {
        //协程对viewModel
        viewModelScope.launch {
            val result = RequestForecastCommand(zipCode).execute()
            data.postValue(result)
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun setData2(zipCode: Long) = viewModelScope.launch {
        RequestForecastCommand(zipCode).execute2()
            .onStart {
                _loading.postValue(true)
            }
            .catch {
                //TODO 捕获上游出现的异常
                _loading.postValue(false)
            }
            .onCompletion {
                _loading.postValue(false)
            }
            .collectLatest {
                print(toJson(it, moshi))
                data.postValue(it)
            }
    }

    fun getItem(position : Int) : Forecast {
        return data.value!![position]
    }
}