package com.qtk.kotlintest.extensions

import android.annotation.SuppressLint
import android.util.TypedValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import com.qtk.kotlintest.App
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

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

fun Double.toDp(): Int {
    val scale: Float = App.instance.applicationContext.resources.displayMetrics.scaledDensity
    return (this / scale + 0.5f).toInt()
}

inline fun <reified T> T.dpToPx(): Float {
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

inline fun <reified T> T.spToPx(): Int {
    val value = when (T::class) {
        Float::class -> this as Float
        Int::class -> this as Int
        else -> throw IllegalStateException("Type not supported")
    }
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        value.toFloat(), App.instance.applicationContext.resources.displayMetrics
    ).toInt()
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

/**
 * dataStore取数据需设置默认值
 */
@Suppress("UNCHECKED_CAST")
fun<T> DataStore<Preferences>.getData(name: String, default: T): Flow<T> {
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
                is Long -> it[preferencesKey<Long>(name)] ?: default
                is String -> it[preferencesKey<String>(name)] ?: default
                is Int -> it[preferencesKey<Int>(name)] ?: default
                is Float -> it[preferencesKey<Float>(name)] ?: default
                is Double -> it[preferencesKey<Double>(name)] ?: default
                is Boolean -> it[preferencesKey<Boolean>(name)] ?: default
                else -> throw IllegalArgumentException(
                    "This type can be saved into Preferences")
            } as T
        }
}

/**
 * dataStore取数据
 */
@Suppress("UNCHECKED_CAST")
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
                Long::class -> it[preferencesKey<Long>(name)] ?: 0L
                String::class -> it[preferencesKey<String>(name)] ?: ""
                Int::class -> it[preferencesKey<Int>(name)] ?: 0
                Float::class -> it[preferencesKey<Float>(name)] ?: 0f
                Double::class -> it[preferencesKey<Double>(name)] ?: 0.0
                Boolean::class -> it[preferencesKey<Boolean>(name)] ?: false
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
            is Long -> it[preferencesKey<Long>(name)] = value as Long
            is String -> it[preferencesKey<String>(name)] = value as String
            is Int -> it[preferencesKey<Int>(name)] = value as Int
            is Float -> it[preferencesKey<Float>(name)] = value as Float
            is Double -> it[preferencesKey<Double>(name)] = value as Double
            is Boolean -> it[preferencesKey<Boolean>(name)] = value as Boolean
            else -> throw IllegalArgumentException(
                "This type can be saved into Preferences")
        }
    }
}