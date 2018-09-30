package com.vbazh.navimeet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.vbazh.navimeet.di.ComponentManager
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Inject
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import android.content.Intent
import android.util.Log


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    private val navigator = object : SupportAppNavigator(this, R.id.main_container) {
        override fun applyCommands(commands: Array<Command>) {
            super.applyCommands(commands)
            supportFragmentManager.executePendingTransactions()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        ComponentManager.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val intent = intent
        val data = intent.data

        if (data!=null) {

            val userId = data.lastPathSegment
            router.newRootScreen(Screens.ProfileScreen(userId))
        } else {

            router.newRootScreen(Screens.LoginScreen())
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}
