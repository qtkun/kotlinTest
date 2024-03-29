package com.qtk.kotlintest.extensions

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.IllegalStateException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class NotNullSingleValueVar<T> {
    @Volatile
    private var value : T? = null
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("${property.name} not initialized")
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (this.value == null) {
            synchronized(this) {
                if (this.value == null) {
                    this.value = value
                }
            }
        }
        /*this.value = if (this.value == null) value
        else throw IllegalStateException("${property.name} already initialized")*/
    }
}

object DelegatesExt {
    fun <T> notNullSingleValue() = NotNullSingleValueVar<T>()
    fun <T> preference(context: Context, name: String, default: T) = Preference(context, name, default)
}

class Preference<T>(private val context: Context, val name: String, private val default: T) {
    private val preferences : SharedPreferences by lazy { context.getSharedPreferences("default", Context.MODE_PRIVATE) }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name, default)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> findPreference(name: String, default: T) : T  = with(preferences) {
        val res : Any? = when (default){
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Float -> getFloat(name, default)
            is Boolean -> getBoolean(name, default)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }
        res as T
    }

    private fun putPreference(name: String, value: T) = with(preferences.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }.apply()
    }
}

interface IDataStoreOwner {
    val context: Context
    val storeName: String
    val scope: CoroutineScope
    val dataStore: DataStore<Preferences>
}

open class DataStoreOwner(override val context: Context, override val storeName: String): IDataStoreOwner {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override val dataStore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.create(
            corruptionHandler = null,
            migrations = listOf(),
            scope = scope
        ) {
            context.preferencesDataStoreFile(storeName)
        }
    }
}

fun <T>storeData(default: T) = DataStoreProperty(default)

class DataStoreProperty<T>(private val default: T): ReadWriteProperty<IDataStoreOwner, T> {
    override fun getValue(thisRef: IDataStoreOwner, property: KProperty<*>): T {
        return runBlocking {
            thisRef.dataStore.getData(property.name, default).first()
        }
    }

    override fun setValue(thisRef: IDataStoreOwner, property: KProperty<*>, value: T) {
        thisRef.scope.launch {
            thisRef.dataStore.putData(property.name, value)
        }
    }

}