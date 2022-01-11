package com.example.ximalaya.other

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.ximalaya.other.Constants.SHP_DATASTORE
import java.text.DecimalFormat

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(SHP_DATASTORE)

fun Context.isConnectedNetwork() =
    (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        getNetworkCapabilities(activeNetwork)?.hasCapability(NET_CAPABILITY_VALIDATED) ?: false
    }

fun Long.convertNumber() = when (toString().length) {
    in 0..4 -> toString()
    in 5..8 -> "${DecimalFormat("0.#").format(toDouble() / 10000)}万"
    else -> "${DecimalFormat("0.#").format(toDouble() / 100000000)}亿"
}