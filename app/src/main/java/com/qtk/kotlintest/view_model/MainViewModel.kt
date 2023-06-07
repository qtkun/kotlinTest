package com.qtk.kotlintest.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qtk.kotlintest.App
import com.qtk.kotlintest.contant.DEFAULT_ZIP
import com.qtk.kotlintest.contant.ZIP_CODE
import com.qtk.kotlintest.domain.command.RequestForecastCommand
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.domain.model.ForecastList
import com.qtk.kotlintest.extensions.getData
import com.qtk.kotlintest.extensions.toJson
import com.qtk.kotlintest.repository.CommonRepository
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val moshi: Moshi
) : ViewModel() {
    private val forecast = MediatorLiveData<ForecastList>()
    val forecastList: LiveData<ForecastList> = forecast
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    init {
        viewModelScope.launch {
            forecast.addSource(getZipCode()) {
                setData2(it)
            }
        }
    }

    fun getZipCode(): LiveData<Long> =
        App.instance.dataStore.getData(ZIP_CODE, DEFAULT_ZIP).asLiveData(viewModelScope.coroutineContext)

    suspend fun setData(zipCode: Long) {
        //协程对viewModel
        viewModelScope.launch {
            val result = RequestForecastCommand(zipCode).execute()
            forecast.postValue(result)
        }
    }

    fun setData2(zipCode: Long) = viewModelScope.launch {
        RequestForecastCommand(zipCode).execute2()
            .onStart {
                _loading.postValue(true)
            }
            .catch {
                //TODO 捕获上游出现的异常
                it.printStackTrace()
                _loading.postValue(false)
            }
            .onCompletion {
                _loading.postValue(false)
            }
            .collectLatest {
                print(moshi.toJson(it))
                forecast.postValue(it)
            }
    }

    fun setData3(): LiveData<ForecastList> =
        App.instance.dataStore.getData(ZIP_CODE, DEFAULT_ZIP).map {
            RequestForecastCommand(it).execute()
        }.asLiveData()

    fun getItem(position: Int): Forecast {
        return forecast.value!![position]
    }
}