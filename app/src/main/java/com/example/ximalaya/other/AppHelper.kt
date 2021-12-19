package com.example.ximalaya.other

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.text.DecimalFormat

fun Context.isConnectedNetwork() =
    (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        getNetworkCapabilities(activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }

fun Long.convertNumber() = when (toString().length) {
    in 0..4 -> toString()
    in 5..8 -> "${DecimalFormat("0.#").format(toDouble() / 10000)}万"
    else -> "${DecimalFormat("0.#").format(toDouble() / 100000000)}亿"
}