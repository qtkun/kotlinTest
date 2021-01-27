package com.qtk.kotlintest.modules

import android.content.Context
import com.qtk.kotlintest.api.Api
import com.qtk.kotlintest.contant.BASE_URL
import com.qtk.kotlintest.paging.CommonRepository
import com.qtk.kotlintest.retrofit.adapter.ApiResultCallAdapterFactory
import com.qtk.kotlintest.retrofit.service.ApiService
import com.qtk.kotlintest.view_model.DetailViewModel
import com.qtk.kotlintest.view_model.GoodsViewModel
import com.qtk.kotlintest.view_model.MainViewModel
import okhttp3.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { DetailViewModel() }
    viewModel { GoodsViewModel(get()) }
}

val appModule = module {
    single {
        val httpCacheDirectory = File(get<Context>().cacheDir, "OkHttpCache")
        val cache = Cache(httpCacheDirectory, 10 * 1024 * 1024)
        val cookieJar = object : CookieJar {
            val cookieStore: HashMap<HttpUrl, List<Cookie>> = HashMap()

            override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                val cookies = cookieStore[url] //取出cookie
                return cookies as MutableList<Cookie>? ?: mutableListOf()
            }

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                cookieStore[url] = cookies;//保存cookie
            }
        }
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(getRequestHeader())
            .addInterceptor(commonInterceptor())
            .addInterceptor(getHttpLoggingInterceptor())
            .addInterceptor(getCacheInterceptor())
            .addNetworkInterceptor(getCacheInterceptor())
            .cache(cache)
            .cookieJar(cookieJar)
            .build()
    }
    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
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
}