package com.qtk.kotlintest.modules

import android.content.Context
import com.google.gson.Gson
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
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(getRequestHeader())
            .addInterceptor(commonInterceptor())
            .addInterceptor(getHttpLoggingInterceptor())
            .addInterceptor(getCacheInterceptor())
            .addNetworkInterceptor(getCacheInterceptor())
            .cache(getCache(context))
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

    @Singleton
    @Provides
    fun provideGson(): Gson = Gson()
}