package com.dragon.tribe.fire.oc.maker.ui

import android.app.Application
import android.content.Context

class App : Application() {

    companion object {
        lateinit var instance: App
            private set

        val context: Context
            get() = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
