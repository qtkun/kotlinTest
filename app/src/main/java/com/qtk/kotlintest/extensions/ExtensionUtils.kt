package com.qtk.kotlintest.extensions

import com.google.gson.Gson
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

fun createBody(map: Map<String, Any>) : RequestBody {
    return Gson().toJson(map).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}