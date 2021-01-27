package com.qtk.kotlintest.extensions

import com.google.gson.Gson
import com.qtk.kotlintest.App
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.DateFormat
import java.util.*

/**
 * Created by qtkun
on 2020-06-16.
 */
fun Long.toDateString(dateFormat: Int = DateFormat.MEDIUM): String {
    val df = DateFormat.getDateInstance(dateFormat, Locale.getDefault())
    return df.format(this)
}

fun Map<String, Any>.createBody() : RequestBody {
    return Gson().toJson(this).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}

fun Double.toPx() : Int{
    val scale: Float = App.instance.applicationContext.resources.displayMetrics.scaledDensity
    return (this * scale + 0.5f).toInt()
}

fun Double.toDp() : Int{
    val scale: Float = App.instance.applicationContext.resources.displayMetrics.scaledDensity
    return (this / scale + 0.5f).toInt()
}