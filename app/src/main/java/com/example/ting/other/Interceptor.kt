package com.example.ting.other

import android.content.Context
import android.webkit.WebSettings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ting.other.Constants.COOKIE_DATASTORE
import com.example.ting.other.Constants.DOMAIN
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*

class HttpsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().let {
            it.newBuilder()
                .url(
                    it.url.newBuilder()
                        .scheme("https")
                        .build()
                ).build()
        }
        return chain.proceed(request)
    }
}

class UserAgentInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .header("User-Agent", WebSettings.getDefaultUserAgent(context))
            .build()
        return chain.proceed(request)
    }
}

val Context.cookieDataStore: DataStore<Preferences> by preferencesDataStore(COOKIE_DATASTORE)

class CookieHelper(private val context: Context) : CookieJar {
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return runBlocking {
            context.cookieDataStore.data.first().asMap().mapNotNull {
                Cookie.Builder()
                    .domain(DOMAIN)
                    .name(it.key.name)
                    .value(it.value.toString())
                    .build()
            }.toMutableList().apply {
                if (none { it.name == "os" }) {
                    add(
                        Cookie.Builder()
                            .domain(DOMAIN)
                            .name("os")
                            .value("pc")
                            .build()
                    )
                }
                if (none { it.name == "appver" }) {
                    add(
                        Cookie.Builder()
                            .domain(DOMAIN)
                            .name("appver")
                            .value("2.7.1.198277")
                            .build()
                    )
                }
            }
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        runBlocking {
            context.cookieDataStore.edit {
                cookies.filter {
                    it.domain == DOMAIN
                }.forEach { cookie ->
                    it[stringPreferencesKey(cookie.name)] = cookie.value
                }
            }
        }
    }
}