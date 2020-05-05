package com.dsp.androidsample

import android.app.Application
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.dsp.androidsample.Logger.d

class AndroidApplication : Application() {
    override fun onCreate() {
        d { "onCreate" }
        super.onCreate()

        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(ApplicationObserver())
    }

    inner class ApplicationObserver : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onEnterForeground() {
            d { "onEnterForeground" }
            stopService(Intent(this@AndroidApplication, MainService::class.java))
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onEnterBackground() {
            d { "onEnterBackground" }
            startService(Intent(this@AndroidApplication, MainService::class.java))
        }
    }
}
