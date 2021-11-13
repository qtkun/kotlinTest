package com.qtk.kotlintest.modules

import android.content.Context
import android.util.Log
import com.qtk.kotlintest.retrofit.adapter.ApiException
import com.qtk.kotlintest.utils.isConnected
import okhttp3.*
import okhttp3.Interceptor.Companion.invoke
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

fun getCache(context: Context) : Cache{
    val httpCacheDirectory = File(context.cacheDir, "OkHttpCache")
    return Cache(httpCacheDirectory, 10 * 1024 * 1024)
}

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

fun getRequestHeader(): Interceptor {
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

fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor { message -> Log.i("qtk", message) }.setLevel(HttpLoggingInterceptor.Level.BODY)
}

fun commonInterceptor(): Interceptor {
    return invoke {
        val originRequest: Request = it.request()
        val request: Request
        val httpUrl =
            originRequest.url.newBuilder()
                .build()
        request = originRequest.newBuilder().url(httpUrl).build()
        val response = it.proceed(request)

        //http status不是成功的情况下，我们不处理
        if (!response.isSuccessful) {
            return@invoke response
        }
        val responseBody = response.body!!
        val source = responseBody.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        val buffer = source.buffer
        val contentType = responseBody.contentType()
        val charset: Charset =
            contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
        val resultString = buffer.clone().readString(charset)


        val jsonObject = JSONObject(resultString)
        if (jsonObject.has("code")) {
            return@invoke response
        }

        val errorCode = jsonObject.optInt("code")
        val errorMsg = jsonObject.optString("msg")
        //对于业务成功的情况不做处理
        if (errorCode == 0) {
            return@invoke response
        }
        throw ApiException(errorCode, errorMsg, request.url.toUri().toString())
    }
}

fun getCacheInterceptor(): Interceptor {
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