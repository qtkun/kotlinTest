package com.qtk.kotlintest.modules

import android.app.Application
import androidx.datastore.preferences.createDataStore
import com.google.gson.Gson
import com.qtk.kotlintest.api.Api
import com.qtk.kotlintest.contant.BASE_URL
import com.qtk.kotlintest.paging.CommonRepository
import com.qtk.kotlintest.retrofit.adapter.ApiResultCallAdapterFactory
import com.qtk.kotlintest.retrofit.service.ApiService
import com.qtk.kotlintest.view_model.DetailViewModel
import com.qtk.kotlintest.view_model.PokemonViewModel
import com.qtk.kotlintest.view_model.MainViewModel
import com.squareup.moshi.Moshi
import okhttp3.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { DetailViewModel() }
    viewModel { PokemonViewModel(get()) }
}

val appModule = module {
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(getRequestHeader())
            .addInterceptor(commonInterceptor())
            .addInterceptor(getHttpLoggingInterceptor())
            .addInterceptor(getCacheInterceptor())
            .addNetworkInterceptor(getCacheInterceptor())
            .cache(getCache(get()))
            .cookieJar(cookieJar)
            .build()
    }
    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
    single {
        Api(get())
    }
    factory {
        CommonRepository(get())
    }
    single {
        get<Retrofit>().create(ApiService::class.java)
    }
    single { Gson() }
    single { Moshi.Builder().build() }
    single { get<Application>().createDataStore(name = "DataStore") }
}