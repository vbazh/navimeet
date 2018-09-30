package com.vbazh.navimeet

import android.app.Application
import com.vbazh.navimeet.di.ComponentManager

class App() : Application() {

    override fun onCreate() {
        super.onCreate()
        ComponentManager.initAppComponent(this)

    }
}