package com.example.ting.other

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.RecyclerView
import com.example.ting.init.AppInitializer
import com.example.ting.other.Constants.APP_SECRET
import com.example.ting.other.Constants.SHP_DATASTORE
import com.ximalaya.ting.android.opensdk.httputil.util.BASE64Encoder
import com.ximalaya.ting.android.opensdk.httputil.util.HMACSHA1
import com.ximalaya.ting.android.player.MD5
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

fun String.sig(): String = MD5.md5(HMACSHA1.HmacSHA1Encrypt(BASE64Encoder.encode(this), APP_SECRET))

fun String.toast() = Toast.makeText(AppInitializer.mContext, this, Toast.LENGTH_SHORT).show()

fun RecyclerView.setOnItemClickListener(listener: (Int, RecyclerView.ViewHolder) -> Unit) {
    addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
        val gestureDetector = GestureDetector(context, object : GestureDetector.OnGestureListener {
            override fun onDown(p0: MotionEvent?) = false

            override fun onShowPress(p0: MotionEvent?) {
            }

            override fun onSingleTapUp(p0: MotionEvent?): Boolean {
                p0?.let {
                    findChildViewUnder(it.x, it.y)?.let { child ->
                        listener(getChildAdapterPosition(child), getChildViewHolder(child))
                    }
                }
                return false
            }

            override fun onScroll(
                p0: MotionEvent?,
                p1: MotionEvent?,
                p2: Float,
                p3: Float
            ) = false

            override fun onLongPress(p0: MotionEvent?) {
            }

            override fun onFling(
                p0: MotionEvent?,
                p1: MotionEvent?,
                p2: Float,
                p3: Float
            ) = false
        })

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            gestureDetector.onTouchEvent(e)
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        }
    })
}