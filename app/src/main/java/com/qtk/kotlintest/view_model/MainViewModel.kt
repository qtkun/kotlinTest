package com.qtk.kotlintest.view_model

import androidx.lifecycle.*
import com.qtk.kotlintest.domain.command.RequestForecastCommand
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.domain.model.ForecastList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
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

    @ExperimentalCoroutinesApi
    suspend fun setData2(zipCode: Long) : LiveData<ForecastList> =
        RequestForecastCommand(zipCode).execute2()
            .onStart {
                //TODO 在调用 flow 请求数据之前，做一些准备工作，例如显示正在加载数据的按钮
            }
            .catch {
                //TODO 捕获上游出现的异常
            }
            .onCompletion {
                //TODO 请求完成
            }
            .asLiveData()

    fun getItem(position : Int) : Forecast {
        return data.value!![position]
    }
}