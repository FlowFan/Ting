package com.example.ximalaya.init

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer
import com.example.ximalaya.other.Constants.KEY_LAST_OAID
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
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
        if (DTransferConstants.isRelease) {
            val mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af"
            mXimalaya.apply {
                setAppkey("9f9ef8f10bebeaa83e71e62f935bede8")
                setPackid("com.app.test.android")
                init(context, mAppSecret, true, getDeviceInfoProvider(context))
            }
        } else {
            val mAppSecret = "0a09d7093bff3d4947a5c4da0125972e"
            mXimalaya.apply {
                setAppkey("f4d8f65918d9878e1702d49a8cdf0183")
                setPackid("com.ximalaya.qunfeng")
                init(context, mAppSecret, getDeviceInfoProvider(context))
            }
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