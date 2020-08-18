package com.qtk.kotlintest.retrofit.manager

import android.util.Log
import com.google.gson.Gson
import com.qtk.kotlintest.App
import com.qtk.kotlintest.retrofit.service.Service
import okhttp3.*
import okhttp3.Interceptor.Companion.invoke
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


object Manager {
    private const val BASE_URL = "http://weshoptest.graspyun.com:5000"
    //创建Cache
    private val httpCacheDirectory = File(App.instance.applicationContext.cacheDir, "OkHttpCache")
    private val cache : Cache = Cache(httpCacheDirectory, 10 * 1024 * 1024)
    private val cookieJar = object : CookieJar{
        val cookieStore: HashMap<HttpUrl, List<Cookie>> = HashMap()

        override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
            val cookies = cookieStore[url] //取出cookie
            return cookies as MutableList<Cookie>? ?: mutableListOf()
        }

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url] = cookies;//保存cookie
        }
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(getRequestHeader())
        .addInterceptor(getHttpLoggingInterceptor())
        .addInterceptor(commonParamsInterceptor())
        .cache(cache)
        .cookieJar(cookieJar)
//        .addNetworkInterceptor(getCacheInterceptor())
//        .addInterceptor(getCacheInterceptor())
        .build()

    val service : Service = Retrofit.Builder()
    .client(client)
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build().create(Service :: class.java)

    private fun getRequestHeader() : Interceptor {
        return invoke {
            val originalRequest: Request = it.request()
            val builder: Request.Builder = originalRequest.newBuilder()
            builder.addHeader("content-type", "application/json; charset=utf-8")

            val requestBuilder: Request.Builder =
                builder.method(originalRequest.method, originalRequest.body)
            val request: Request = requestBuilder.build()
            it.proceed(request).apply {
                header("time")?.let {
                    Log.i("qtk", "响应时间：$it")
                }
            }
        }
    }

    fun createBody(map: Map<String, Any>) : RequestBody {
        return Gson().toJson(map).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun getHttpLoggingInterceptor() : HttpLoggingInterceptor {
        return HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger{
            override fun log(message: String) {
                Log.i("qtk", message)
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private fun commonParamsInterceptor() : Interceptor {
        return invoke{
            val originRequest: Request = it.request()
            val request: Request
            val httpUrl =
                originRequest.url.newBuilder()
                    .addQueryParameter("clienttype", "bandroid")
                    .addQueryParameter("token", "75b43cd1b5ba47464gQQ10jb2vUJWHeCPmJWKTvhc1toKipTIUFSv2/KdCua5+ijXMh7zEmmR3WvSHsbZQ9JTRo379BKq2p2vWl3j2ZNGGbFYWzvqTaX4M48zzTVCUNW4aU/WxzQnGD1KeAj")
                    .build()
            request = originRequest.newBuilder().url(httpUrl).build()
            it.proceed(request)
        }
    }

    /*private fun getCacheInterceptor() : Interceptor {
        return invoke {
            var request: Request = it.request()
            if (!NetworkUtils.isConnected()) {
                //无网络下强制使用缓存，无论缓存是否过期,此时该请求实际上不会被发送出去。
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE)
                    .build()
            }

            val response: Response = it.proceed(request)
            if (NetworkUtils.isConnected()) { //有网络情况下，根据请求接口的设置，配置缓存。
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
    }*/
}