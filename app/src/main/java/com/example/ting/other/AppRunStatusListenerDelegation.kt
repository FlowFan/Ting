package com.example.ting.other

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.ting.init.MyApplication

class AppRunStatusListenerDelegation private constructor() {
    companion object {
        private val instance: AppRunStatusListenerDelegation by lazy { AppRunStatusListenerDelegation() }

        @JvmStatic
        fun get(): AppRunStatusListenerDelegation = instance
    }

    private var activityNumber = 0
    private val appRunStatusListeners = mutableMapOf<String, AppRunStatusListener>()

    fun register() {
        MyApplication().registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
                if (activityNumber++ == 0) {
                    synchronized(this) {
                        appRunStatusListeners.forEach {
                            it.value.onAppForeground?.invoke()
                        }
                    }
                }
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
                if (--activityNumber == 0) {
                    synchronized(this) {
                        appRunStatusListeners.forEach {
                            it.value.onAppBackground?.invoke()
                        }
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    fun addListener(name: String, appRunStatusListener: AppRunStatusListener) {
        synchronized(this) {
            if (!appRunStatusListeners.contains(name)) {
                appRunStatusListeners[name] = appRunStatusListener
            }
        }
    }

    fun removeListener(name: String) {
        synchronized(this) {
            if (appRunStatusListeners.contains(name)) {
                appRunStatusListeners.remove(name)
            }
        }
    }
}

data class AppRunStatusListener @JvmOverloads constructor(
    val onAppForeground: (() -> Unit)? = null,
    val onAppBackground: (() -> Unit)? = null
)