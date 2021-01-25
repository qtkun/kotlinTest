package com.qtk.kotlintest.utils

import android.content.Context
import android.util.Log
import com.qtk.kotlintest.retrofit.adapter.ApiException
import com.qtk.kotlintest.retrofit.adapter.ApiResultCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.*
import okhttp3.Interceptor.Companion.invoke
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {
    companion object {
        const val BASE_URL = "http://weshoptest.graspyun.com:5000"
    }

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
            .addInterceptor(getHttpLoggingInterceptor())
            .addInterceptor(commonParamsInterceptor())
            .addInterceptor(getErrorInterceptor())
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

    private fun getRequestHeader(): Interceptor {
        return invoke {
            val originalRequest: Request = it.request()
            val builder: Request.Builder = originalRequest.newBuilder()
            builder.addHeader("content-type", "application/json; charset=utf-8")

            val requestBuilder: Request.Builder =
                builder.method(originalRequest.method, originalRequest.body)
            val request: Request = requestBuilder.build()
            it.proceed(request).apply {
                header("time")?.let { time ->
                    Log.i("qtk", "响应时间：$time")
                }
            }
        }
    }

    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.i("qtk", message)
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private fun commonParamsInterceptor(): Interceptor {
        return invoke {
            val originRequest: Request = it.request()
            val request: Request
            val httpUrl =
                originRequest.url.newBuilder()
                    .addQueryParameter("clienttype", "bandroid")
                    .addQueryParameter(
                        "token",
                        "75b43cd1b5ba47464gQQ10jb2vUJWHeCPmJWKTvhc1toKipTIUFSv2/KdCua5+ijXMh7zEmmR3WvSHsbZQ9JTRo379BKq2p2vWl3j2ZNGGbFYWzvqTaX4M48zzTVCUNW4aU/WxzQnGD1KeAj"
                    )
                    .build()
            request = originRequest.newBuilder().url(httpUrl).build()
            it.proceed(request)
        }
    }

    private fun getCacheInterceptor(): Interceptor {
        return invoke {
            var request: Request = it.request()
            if (!isConnected()) {
                //无网络下强制使用缓存，无论缓存是否过期,此时该请求实际上不会被发送出去。
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE)
                    .build()
            }

            val response: Response = it.proceed(request)
            if (isConnected()) { //有网络情况下，根据请求接口的设置，配置缓存。
                //这样在下次请求时，根据缓存决定是否真正发出请求。
                val cacheControl = request.cacheControl.toString()
                //当然如果你想在有网络的情况下都直接走网络，那么只需要
                //将其超时时间这是为0即可:String cacheControl="Cache-Control:public,max-age=0"
                val maxAge = 60 * 60 // read from cache for 1 minute
                response.newBuilder() //                            .header("Cache-Control", cacheControl)
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .removeHeader("Pragma")
                    .build()
            } else {
                //无网络
                val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
                response.newBuilder() //                            .header("Cache-Control", "public,only-if-cached,max-stale=360000")
                    .header("Cache-Control", "public,only-if-cached,max-stale=$maxStale")
                    .removeHeader("Pragma")
                    .build()
            }
            response
        }
    }

    /**
     * 业务错误 Interceptor
     * 对于request: 无
     * 对于response:负责解析业务错误（在http status 成功的前提下）
     */
    private fun getErrorInterceptor() :Interceptor {
        return invoke {
            val response = it.proceed(it.request())
            //http status不是成功的情况下，我们不处理
            if (!response.isSuccessful){
                return@invoke response
            }

            //因为response.body().string() 只能调用一次，所以这里读取responseBody不使用response.body().string()，原因：https://juejin.im/post/6844903545628524551
            //以下读取resultString的代码节选自
            //https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/src/main/kotlin/okhttp3/logging/HttpLoggingInterceptor.kt

            val responseBody = response.body!!
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer
            val contentType = responseBody.contentType()
            val charset: Charset = contentType?.charset(UTF_8) ?: UTF_8
            val resultString = buffer.clone().readString(charset)


            val jsonObject = JSONObject(resultString)
            if (!jsonObject.has("code")) {
                return@invoke response
            }

            val errorCode = jsonObject.optInt("code")
            val errorMsg = jsonObject.optString("msg")
            //对于业务成功的情况不做处理
            if (errorCode == 0) {
                return@invoke response
            }
            throw ApiException(errorCode, errorMsg)
        }
    }
}