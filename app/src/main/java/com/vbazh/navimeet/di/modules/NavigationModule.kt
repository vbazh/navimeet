package com.vbazh.navimeet.di.modules

import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Singleton

@Module
class NavigationModule {

    val cicerone: Cicerone<Router> = Cicerone.create()

    @Provides
    @Singleton
    fun provideRouter(): Router {

        return cicerone.router
    }

    @Provides
    @Singleton
    fun provideNavigatorHolder(): NavigatorHolder {

        return cicerone.navigatorHolder
    }
}