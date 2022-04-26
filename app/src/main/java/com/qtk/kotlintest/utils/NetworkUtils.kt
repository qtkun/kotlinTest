package com.qtk.kotlintest.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.qtk.kotlintest.App

fun isConnected() : Boolean{
    val connectivityManager : ConnectivityManager = App.instance.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network : Network? = connectivityManager.activeNetwork
    network?.let {
        val networkCapabilities : NetworkCapabilities? = connectivityManager.getNetworkCapabilities(it)
        networkCapabilities?.let { capabilities ->
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            }
        }
    }
    return false
}

fun Context.wifiConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivityManager.activeNetwork?.let { network ->
        connectivityManager.getNetworkCapabilities(network)?.let {
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            }
        }
    }
    return false
}