package com.example.ting.init

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer
import com.example.ting.other.Constants.APP_KEY
import com.example.ting.other.Constants.APP_SECRET
import com.example.ting.other.Constants.PACK_ID
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.DeviceInfoProviderDefault

@SuppressLint("StaticFieldLeak")
class AppInitializer : Initializer<Unit> {
    companion object {
        lateinit var mContext: Context
            private set
    }

    override fun create(context: Context) {
        mContext = context.applicationContext
        CommonRequest.getInstanse().apply {
            setAppkey(APP_KEY)
            setPackid(PACK_ID)
            init(
                mContext,
                APP_SECRET,
                true,
                object : DeviceInfoProviderDefault(mContext) {
                    override fun oaid(): String = ""
                }
            )
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> =
        mutableListOf()
}