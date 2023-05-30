package com.qtk.kotlintest.modules

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.qtk.kotlintest.contant.BASE_URL
import com.qtk.kotlintest.contant.POKEMON_BASE_URL
import com.qtk.kotlintest.room.ForecastDao
import com.qtk.kotlintest.retrofit.adapter.ApiResultCallAdapterFactory
import com.qtk.kotlintest.retrofit.service.ApiService
import com.qtk.kotlintest.retrofit.service.ChatGPTService
import com.qtk.kotlintest.room.PokemonDao
import com.qtk.kotlintest.room.AppDataBase
import com.qtk.kotlintest.room.ChatGPTDao
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
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
    @ChatGPTRetrofit
    @Provides
    fun provideChatGPTRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @PokemonRetrofit
    @Provides
    fun providePokemonRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(POKEMON_BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(@PokemonRetrofit retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideChatGPTService(@ChatGPTRetrofit retrofit: Retrofit): ChatGPTService {
        return retrofit.create(ChatGPTService::class.java)
    }

    @Singleton
    @Provides
    fun provideGson(): Gson = Gson()

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Singleton
    @Provides
    fun provideAppDataBase(@ApplicationContext context: Context): AppDataBase =
        Room.databaseBuilder(context, AppDataBase::class.java, "kotlin_text_room.db")
            .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun providePokemonDao(appDataBase: AppDataBase): PokemonDao = appDataBase.getPokemonDao()

    @Singleton
    @Provides
    fun provideForecastDao(appDataBase: AppDataBase): ForecastDao = appDataBase.getForecastDao()

    @Singleton
    @Provides
    fun provideChatGPTDao(appDataBase: AppDataBase): ChatGPTDao = appDataBase.getChatGPTDao()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChatGPTRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PokemonRetrofit

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ForecastDaoEntryPoint{
    fun getForecastDao(): ForecastDao
}