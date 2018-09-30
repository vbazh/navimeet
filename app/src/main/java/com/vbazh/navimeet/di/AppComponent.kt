package com.vbazh.navimeet.di

import com.vbazh.navimeet.MainActivity
import com.vbazh.navimeet.di.modules.NavigationModule
import com.vbazh.navimeet.di.modules.PresentationModule
import com.vbazh.navimeet.di.modules.RemoteDataModule
import com.vbazh.navimeet.ui.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NavigationModule::class, PresentationModule::class, RemoteDataModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(loginFragment: LoginFragment)

    fun inject(contactsFragment: ContactsFragment)

    fun inject(profileFragment: ProfileFragment)

    fun inject(intervalFragment: IntervalFragment)

    fun inject(addIntervalFragment: AddIntervalFragment)
}