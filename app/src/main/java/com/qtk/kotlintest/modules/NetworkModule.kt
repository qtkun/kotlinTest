package com.qtk.kotlintest.modules

import android.content.Context
import com.qtk.kotlintest.contant.BASE_URL
import com.qtk.kotlintest.retrofit.adapter.ApiResultCallAdapterFactory
import com.qtk.kotlintest.retrofit.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        //创建Cache
        val httpCacheDirectory = File(context.cacheDir, "OkHttpCache")
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
        return OkHttpClient.Builder()
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

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}