package com.example.ting.init

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer
import com.example.ting.other.Constants.APP_KEY
import com.example.ting.other.Constants.APP_SECRET
import com.example.ting.other.Constants.KEY_LAST_OAID
import com.example.ting.other.Constants.PACK_ID
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.DeviceInfoProviderDefault
import com.ximalaya.ting.android.opensdk.datatrasfer.IDeviceInfoProvider
import com.ximalaya.ting.android.opensdk.util.SharedPreferencesUtil

@SuppressLint("StaticFieldLeak")
class AppInitializer : Initializer<Unit> {
    companion object {
        lateinit var mContext: Context
            private set
    }

    private lateinit var oaid: String

    override fun create(context: Context) {
        val mXimalaya = CommonRequest.getInstanse()
        mContext = context.applicationContext
        oaid = SharedPreferencesUtil.getInstance(context.applicationContext)
            .getString(KEY_LAST_OAID)
        mXimalaya.apply {
            setAppkey(APP_KEY)
            setPackid(PACK_ID)
            init(context, APP_SECRET, true, getDeviceInfoProvider(context))
        }
    }

    private fun getDeviceInfoProvider(context: Context): IDeviceInfoProvider {
        return object : DeviceInfoProviderDefault(context) {
            override fun oaid(): String {
                return oaid
            }
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}