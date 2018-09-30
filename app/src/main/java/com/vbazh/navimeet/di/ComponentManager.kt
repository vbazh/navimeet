package com.vbazh.navimeet.di

import android.content.Context

val ComponentManager: ComponentManagerImpl by lazy { ComponentManagerImpl() }

class ComponentManagerImpl {

    private lateinit var appComponent: AppComponent

    fun initAppComponent(context: Context) {
        appComponent = DaggerAppComponent.builder()
            .build()
    }

    fun getAppComponent() = appComponent

}