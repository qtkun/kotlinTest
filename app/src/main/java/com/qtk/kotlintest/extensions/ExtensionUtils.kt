package com.qtk.kotlintest.extensions

import android.util.TypedValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import com.google.gson.Gson
import com.qtk.kotlintest.App
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.lang.reflect.Type
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

fun Map<String, Any>.createBody(): RequestBody {
    return Gson().toJson(this).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}

fun Double.toPx(): Int {
    val scale: Float = App.instance.applicationContext.resources.displayMetrics.scaledDensity
    return (this * scale + 0.5f).toInt()
}

fun Double.toDp(): Int {
    val scale: Float = App.instance.applicationContext.resources.displayMetrics.scaledDensity
    return (this / scale + 0.5f).toInt()
}

inline fun <reified T> T.dpToPx(): Int {
    val value = when (T::class) {
        Float::class -> this as Float
        Int::class -> this as Int
        else -> throw IllegalStateException("Type not supported")
    }
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(), App.instance.applicationContext.resources.displayMetrics
    ).toInt()
}

inline fun <reified T> buildJsonAdapter(moshi: Moshi): JsonAdapter<T> = moshi.adapter(T::class.java)

inline fun <reified T> buildListJsonAdapter(moshi: Moshi): JsonAdapter<List<T>> {
    val type: Type = Types.newParameterizedType(List::class.java, T::class.java)
    return moshi.adapter(type)
}

inline fun <reified K, reified V> buildMapJsonAdapter(moshi: Moshi): JsonAdapter<Map<K, V>> {
    val type: Type = Types.newParameterizedType(Map::class.java, K::class.java, V::class.java)
    return moshi.adapter(type)
}

inline fun <reified T> fromJson(json: String, moshi: Moshi): T? {
    val jsonAdapter: JsonAdapter<T> = buildJsonAdapter(moshi)
    return jsonAdapter.fromJson(json)
}

inline fun <reified T> fromJsonList(json: String, moshi: Moshi): List<T>? {
    val jsonAdapter: JsonAdapter<List<T>> = buildListJsonAdapter(moshi)
    return jsonAdapter.fromJson(json)
}

inline fun <reified T> toJson(t: T, moshi: Moshi): String {
    val jsonAdapter: JsonAdapter<T> = buildJsonAdapter(moshi)
    return jsonAdapter.toJson(t)
}

inline fun <reified T> toJsonList(t: List<T>, moshi: Moshi): String {
    val jsonAdapter: JsonAdapter<List<T>> = buildListJsonAdapter(moshi)
    return jsonAdapter.toJson(t)
}

inline fun <reified K, reified V> toJsonMap(t: Map<K, V>, moshi: Moshi): String {
    val jsonAdapter: JsonAdapter<Map<K, V>> = buildMapJsonAdapter(moshi)
    return jsonAdapter.toJson(t)
}

@Suppress("UNCHECKED_CAST")
fun<T> findDataStore(dataStore: DataStore<Preferences>, name: String, default: T): Flow<T> {
    return dataStore.data
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
                is Boolean -> it[preferencesKey<Boolean>(name)] ?: default
                else -> throw IllegalArgumentException(
                    "This type can be saved into Preferences")
            } as T
        }
}

suspend fun<T> putDataStore(dataStore: DataStore<Preferences>, name: String, value: T) = with(dataStore) {
    edit {
        when(value){
            is Long -> it[preferencesKey<Long>(name)] = value as Long
            is String -> it[preferencesKey<String>(name)] = value as String
            is Int -> it[preferencesKey<Int>(name)] = value as Int
            is Float -> it[preferencesKey<Float>(name)] = value as Float
            is Boolean -> it[preferencesKey<Boolean>(name)] = value as Boolean
            else -> throw IllegalArgumentException(
                "This type can be saved into Preferences")
        }
    }
}