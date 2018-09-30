package com.vbazh.navimeet.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vbazh.navimeet.R
import com.vbazh.navimeet.di.ComponentManager
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class AddIntervalFragment : Fragment() {

    companion object {

        const val TAG = "AddIntervalFragment"

        const val PROFILE_ID = "profile_id"

        fun newInstance(idUser: String): AddIntervalFragment {
            val fragment = AddIntervalFragment()
            val args = Bundle()
            args.putString(PROFILE_ID, idUser)
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentManager.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_interval, container, false)
    }
}