package com.example.goalguru.base

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Globals.context = applicationContext
    }

    object Globals {
        var context: Context? = null
    }
}