package com.digigames_interactive.smack.Controller

import android.app.Application
import com.digigames_interactive.smack.Utilities.SharedPrefs

class App : Application(){

    companion object {
        lateinit var prefs: SharedPrefs
    }

    override fun onCreate() {
        super.onCreate()

        prefs = SharedPrefs(applicationContext)
    }
}