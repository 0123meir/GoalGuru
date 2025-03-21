package com.example.goalguru.base

import android.app.Application
import android.content.Context
import com.cloudinary.android.MediaManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Globals.context = applicationContext

        // Initialize Cloudinary
        val config: HashMap<String, String> = HashMap()
        config["cloud_name"] = "dgfkcu0ww"
        config["api_key"] = "463671991214375"
        config["api_secret"] = "FJ5sPBBA0ucHEausTA-Yz5dtA1w"
        MediaManager.init(this, config)
    }

    object Globals {
        var context: Context? = null
    }
}