package com.qtk.kotlintest.extensions

import android.annotation.SuppressLint
import android.util.TypedValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.qtk.kotlintest.App
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by qtkun
on 2020-06-16.
 */
fun Long.toDateString(dateFormat: Int = DateFormat.MEDIUM): String {
    val df = DateFormat.getDateInstance(dateFormat, Locale.getDefault())
    return df.format(this)
}

fun Long.toDateString(): String {
    val df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    return df.format(this)
}

@SuppressLint("NewApi")
fun getYesterdayMillis(): Long {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val yesterday = LocalDate.now().minusDays(1).toString()
    return sdf.parse(yesterday)?.time ?: 0L
}

@SuppressLint("NewApi")
fun getTodayMillis(): Long {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = LocalDate.now().toString()
    return sdf.parse(today)?.time ?: 0L
}

fun Double.toPx(): Int {
    val scale: Float = App.instance.applicationContext.resources.displayMetrics.scaledDensity
    return (this * scale + 0.5f).toInt()
}

fun Float.toDp(): Int {
    val scale: Float = App.instance.applicationContext.resources.displayMetrics.scaledDensity
    return (this / scale + 0.5f).toInt()
}

fun Int.toHex(): String {
    val hex = Integer.toHexString(this)
    return if (hex.length % 2 != 0) { "0x0${hex}" } else { "0x$hex" }
}

fun String.binaryToHex(): String {
    val binary = Integer.parseInt(this, 2)
    val hex = Integer.toHexString(binary and 0xFF)
    return if (hex.length == 1) { "0x0${hex}" } else { "0x$hex" }
}

inline fun <reified T> T.dp(): Float {
    val value = when (T::class) {
        Float::class -> this as Float
        Int::class -> this as Int
        else -> throw IllegalStateException("Type not supported")
    }
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(), App.instance.applicationContext.resources.displayMetrics
    )
}

inline fun <reified T> T.spToPx(): Float {
    val value = when (T::class) {
        Float::class -> this as Float
        Int::class -> this as Int
        else -> throw IllegalStateException("Type not supported")
    }
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        value.toFloat(), App.instance.applicationContext.resources.displayMetrics
    )
}

inline fun <reified T : Number> T.dpToPx(): T {
    val px = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(), App.instance.applicationContext.resources.displayMetrics
    )
    return when (T::class) {
        Float::class -> px as T
        Double::class -> px.toDouble() as T
        Int::class -> px.toInt() as T
        else -> throw IllegalStateException("Type not supported")
    }
}

inline fun <reified T : Number> T.spToPx(): T {
    val px = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(), App.instance.applicationContext.resources.displayMetrics
    )
    return when (T::class) {
        Float::class -> px as T
        Double::class -> px.toDouble() as T
        Int::class -> px.toInt() as T
        else -> throw IllegalStateException("Type not supported")
    }
}

inline fun <reified T> Moshi.buildJsonAdapter(): JsonAdapter<T> = this.adapter(T::class.java)

inline fun <reified T> Moshi.buildListJsonAdapter(): JsonAdapter<List<T>> {
    val type: Type = Types.newParameterizedType(List::class.java, T::class.java)
    return this.adapter(type)
}

inline fun <reified K, reified V> Moshi.buildMapJsonAdapter(): JsonAdapter<Map<K, V>> {
    val type: Type = Types.newParameterizedType(Map::class.java, K::class.java, V::class.java)
    return this.adapter(type)
}

inline fun <reified T> Moshi.fromJson(json: String): T? {
    val jsonAdapter: JsonAdapter<T> = buildJsonAdapter()
    return jsonAdapter.fromJson(json)
}

inline fun <reified T> Moshi.fromJsonList(json: String): List<T>? {
    val jsonAdapter: JsonAdapter<List<T>> = buildListJsonAdapter()
    return jsonAdapter.fromJson(json)
}

inline fun <reified K, reified V> Moshi.fromJsonMap(json: String): Map<K, V>? {
    val jsonAdapter: JsonAdapter<Map<K, V>> = buildMapJsonAdapter()
    return jsonAdapter.fromJson(json)
}

inline fun <reified T> Moshi.toJson(t: T): String {
    val jsonAdapter: JsonAdapter<T> = buildJsonAdapter()
    return jsonAdapter.toJson(t)
}

inline fun <reified T> Moshi.toJsonList(t: List<T>): String {
    val jsonAdapter: JsonAdapter<List<T>> = buildListJsonAdapter()
    return jsonAdapter.toJson(t)
}

inline fun <reified K, reified V> Moshi.toJsonMap(t: Map<K, V>): String {
    val jsonAdapter: JsonAdapter<Map<K, V>> = buildMapJsonAdapter()
    return jsonAdapter.toJson(t)
}

/**
 * 构建Retrofit Body参数
 */
fun Moshi.createBody(map: Map<String, Any>): RequestBody {
    return toJsonMap(map).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}

fun random(): String {
    val generator = Random()
    val randomStringBuilder = StringBuilder()
    val randomLength = generator.nextInt(6)
    var tempChar: Char
    for (i in 0 until randomLength) {
        tempChar = (generator.nextInt(96) + 32).toChar()
        randomStringBuilder.append(tempChar)
    }
    return randomStringBuilder.toString()
}

/**
 * dataStore取数据需设置默认值
 */
inline fun<reified T> DataStore<Preferences>.getData(name: String, default: T): Flow<T> {
    return this.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            when(default){
                is Long -> it[longPreferencesKey(name)] ?: default
                is String -> it[stringPreferencesKey(name)] ?: default
                is Int -> it[intPreferencesKey(name)] ?: default
                is Float -> it[floatPreferencesKey(name)] ?: default
                is Double -> it[doublePreferencesKey(name)] ?: default
                is Boolean -> it[booleanPreferencesKey(name)] ?: default
                else -> throw IllegalArgumentException(
                    "This type can be saved into Preferences")
            } as T
        }
}

/**
 * dataStore取数据
 */
inline fun<reified T> DataStore<Preferences>.getData(name: String): Flow<T> {
    return this.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            when(T::class){
                Long::class -> it[longPreferencesKey(name)] ?: 0L
                String::class -> it[stringPreferencesKey(name)] ?: ""
                Int::class -> it[intPreferencesKey(name)] ?: 0
                Float::class -> it[floatPreferencesKey(name)] ?: 0f
                Double::class -> it[doublePreferencesKey(name)] ?: 0.0
                Boolean::class -> it[booleanPreferencesKey(name)] ?: false
                else -> throw IllegalArgumentException(
                    "This type can be saved into Preferences")
            } as T
        }
}

/**
 * dataStore存数据
 */
suspend fun<T> DataStore<Preferences>.putData(name: String, value: T) = with(this) {
    edit {
        when(value){
            is Long -> it[longPreferencesKey(name)] = value as Long
            is String -> it[stringPreferencesKey(name)] = value as String
            is Int -> it[intPreferencesKey(name)] = value as Int
            is Float -> it[floatPreferencesKey(name)] = value as Float
            is Double -> it[doublePreferencesKey(name)] = value as Double
            is Boolean -> it[booleanPreferencesKey(name)] = value as Boolean
            else -> throw IllegalArgumentException(
                "This type can be saved into Preferences")
        }
    }
}
suspend inline fun<reified T> DataStore<Preferences>.getDataAwait(name: String, default: T): T = suspendCoroutine { cont ->
    CoroutineScope(Dispatchers.IO).launch {
        data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
                cont.resumeWithException(it)
            } else {
                throw it
            }
        }.map {
            when(T::class){
                Long::class -> it[longPreferencesKey(name)] ?: default
                String::class -> it[stringPreferencesKey(name)] ?: default
                Int::class -> it[intPreferencesKey(name)] ?: default
                Float::class -> it[floatPreferencesKey(name)] ?: default
                Double::class -> it[doublePreferencesKey(name)] ?: default
                Boolean::class -> it[booleanPreferencesKey(name)] ?: default
                else -> throw IllegalArgumentException(
                    "This type can be saved into Preferences")
            } as T
        }.collect {
            cont.resume(it)
        }
    }
}