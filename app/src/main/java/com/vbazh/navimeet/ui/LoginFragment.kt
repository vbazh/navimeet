package com.vbazh.navimeet.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.vbazh.navimeet.R
import com.vbazh.navimeet.Screens
import com.vbazh.navimeet.di.ComponentManager
import kotlinx.android.synthetic.main.fragment_login.*
import ru.terrakok.cicerone.Router
import javax.inject.Inject
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.vbazh.navimeet.data.User


class LoginFragment : Fragment() {

    @Inject
    lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentManager.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {


            if (login.text.isNotEmpty() && password.text.isNotEmpty()) {


                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("users")

                val user = myRef.child(login.text.toString())

                user.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val userUpdated = dataSnapshot.getValue<User>(User::class.java)

                        when {
                            userUpdated == null -> {

                                myRef.child(login.text.toString())
                                    .setValue(User(login.text.toString(), password.text.toString()))
                            }
                            password.text.toString() == userUpdated.password -> {

                                val sharedprefs = context?.getSharedPreferences("pref_data", Context.MODE_PRIVATE)
                                sharedprefs?.edit()?.putString("user_id", userUpdated.login)?.apply()

                                router.navigateTo(Screens.ContactsScreen())
                            }

                            else -> Toast.makeText(
                                context,
                                "Wrong Password!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        println("The read failed: " + databaseError.code)
                        Toast.makeText(context, "database error!", Toast.LENGTH_SHORT).show()
                    }
                })

            } else {

                Toast.makeText(context, "Incorrect form", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
