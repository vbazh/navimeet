package com.vbazh.navimeet

import android.support.v4.app.Fragment
import com.vbazh.navimeet.ui.ContactsFragment
import com.vbazh.navimeet.ui.IntervalFragment
import com.vbazh.navimeet.ui.LoginFragment
import com.vbazh.navimeet.ui.ProfileFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

class Screens {

    class LoginScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return LoginFragment()
        }
    }

    class ContactsScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return ContactsFragment()
        }
    }

    class ProfileScreen(val idUser: String) : SupportAppScreen() {

        override fun getFragment(): Fragment {
            return ProfileFragment.newInstance(idUser)
        }
    }

    class IntervalScreen(private val idUser: String, private val idInterval: Int) : SupportAppScreen() {

        override fun getFragment(): Fragment {
            return IntervalFragment.newInstance(idUser, idInterval)
        }
    }
}