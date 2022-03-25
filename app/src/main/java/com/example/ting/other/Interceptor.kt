package com.example.ting.other

import android.webkit.WebSettings
import com.example.ting.init.AppInitializer
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class HttpsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().run {
            if (isHttps) {
                this
            } else {
                newBuilder().url(url().toHttps()).build()
            }
        }
        return chain.proceed(request)
    }
}

class UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .header("User-Agent", WebSettings.getDefaultUserAgent(AppInitializer.mContext))
            .build()
        return chain.proceed(request)
    }
}

fun HttpUrl.toHttps() = toString().run {
    if (startsWith("https")) {
        this
    } else {
        replace("http://", "https://")
    }
}