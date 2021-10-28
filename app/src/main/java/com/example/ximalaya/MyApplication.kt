package com.example.ximalaya

import android.app.Application
import android.content.Context
import com.example.ximalaya.Constants.KEY_LAST_OAID
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.DeviceInfoProviderDefault
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.IDeviceInfoProvider
import com.ximalaya.ting.android.opensdk.util.SharedPreferencesUtil
import com.xmlywind.devicehelper.oaId.helpers.DevicesIDsHelper

class MyApplication : Application(), DevicesIDsHelper.AppIdsUpdater {
    private lateinit var oaid: String

    override fun onCreate() {
        super.onCreate()
        val mXimalaya = CommonRequest.getInstanse()
        oaid = SharedPreferencesUtil.getInstance(applicationContext).getString(KEY_LAST_OAID)
        if (DTransferConstants.isRelease) {
            val mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af"
            mXimalaya.apply {
                setAppkey("9f9ef8f10bebeaa83e71e62f935bede8")
                setPackid("com.app.test.android")
                init(
                    this@MyApplication,
                    mAppSecret,
                    true,
                    getDeviceInfoProvider(this@MyApplication)
                )
            }
        } else {
            val mAppSecret = "0a09d7093bff3d4947a5c4da0125972e"
            mXimalaya.apply {
                setAppkey("f4d8f65918d9878e1702d49a8cdf0183")
                setPackid("com.ximalaya.qunfeng")
                init(this@MyApplication, mAppSecret, getDeviceInfoProvider(this@MyApplication))
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

    override fun OnIdsAvalid(p0: String) {
        oaid = p0
        SharedPreferencesUtil.getInstance(applicationContext).saveString(KEY_LAST_OAID, p0)
    }
}
