package com.example.ting.other

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.webkit.WebSettings
import androidx.core.content.edit
import com.example.ting.init.AppInitializer
import okhttp3.*

class HttpsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().run {
            if (isHttps) {
                this
            } else {
                newBuilder().url(url.toHttps()).build()
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

fun sharedPreferencesOf(
    name: String
): SharedPreferences = AppInitializer.mContext.getSharedPreferences(name, Context.MODE_PRIVATE)

class CookieHelper : CookieJar {
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = sharedPreferencesOf("cookie").all.map { (k, v) ->
            Cookie.Builder()
                .domain("music.163.com")
                .name(k)
                .value(v.toString())
                .build()
        }.toMutableList()
        if (!cookies.any {
                it.name == "os"
            }) {
            cookies += Cookie.Builder()
                .domain("music.163.com")
                .name("os")
                .value("pc")
                .build()
        }
        if (!cookies.any {
                it.name == "appver"
            }) {
            cookies += Cookie.Builder()
                .domain("music.163.com")
                .name("appver")
                .value("2.7.1.198277")
                .build()
        }
        return cookies.also {
            Log.d(
                "CookieHelper",
                "loadForRequest: ${cookies.joinToString(separator = ",") { it.name }}"
            )
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        sharedPreferencesOf("cookie").let {
            cookies
                .filter {
                    it.domain == "music.163.com"
                }
                .forEach { cookie ->
                    it.edit {
                        putString(cookie.name, cookie.value)
                        Log.i(
                            "CookieHelper",
                            "saveFromResponse: saved cookie: ${cookie.name}=${cookie.value}"
                        )
                    }
                }
        }
    }
}