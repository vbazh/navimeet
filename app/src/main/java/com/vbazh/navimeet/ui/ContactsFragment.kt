package com.vbazh.navimeet.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.vbazh.navimeet.R
import com.vbazh.navimeet.Screens
import com.vbazh.navimeet.data.User
import com.vbazh.navimeet.di.ComponentManager
import kotlinx.android.synthetic.main.fragment_contacts.*
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class ContactsFragment : Fragment() {

    private var mDatabase: DatabaseReference? = null
    private var mContactsReference: DatabaseReference? = null
    private var mContactsListener: ChildEventListener? = null

    val TAG = "TAG"

    @Inject
    lateinit var router: Router

    private var mAdapter: FirebaseRecyclerAdapter<User, ContactsViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentManager.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mAdapter?.startListening()
    }

    private fun firebaseListenerInit() {

        val childEventListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // A new message has been added
                // onChildAdded() will be called for each node at the first time
                val message = dataSnapshot.getValue(User::class.java)

                Log.e(TAG, "onChildAdded:" + message!!.login)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.e(TAG, "onChildChanged:" + dataSnapshot.key)

                // A message has changed
                val message = dataSnapshot.getValue(User::class.java)
                Toast.makeText(context, "onChildChanged: " + message!!.login, Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.e(TAG, "onChildRemoved:" + dataSnapshot.key)

                // A message has been removed
                val message = dataSnapshot.getValue(User::class.java)
                Toast.makeText(context, "onChildRemoved: " + message!!.login, Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.e(TAG, "onChildMoved:" + dataSnapshot.key)

                // A message has changed position
                val message = dataSnapshot.getValue(User::class.java)
                Toast.makeText(context, "onChildMoved: " + message!!.login, Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "postMessages:onCancelled", databaseError.toException())
                Toast.makeText(context, "Failed to load Message.", Toast.LENGTH_SHORT).show()
            }
        }

        mContactsReference!!.addChildEventListener(childEventListener)

        // copy for removing at onStop()
        mContactsListener = childEventListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().reference
        mContactsReference = FirebaseDatabase.getInstance().getReference("users")

        firebaseListenerInit()

        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = false
        contactsRecycler.setHasFixedSize(true)
        contactsRecycler.layoutManager = layoutManager

        val query = mContactsReference!!.limitToFirst(20)

        mAdapter = object : FirebaseRecyclerAdapter<User, ContactsViewHolder>(
            FirebaseRecyclerOptions.Builder<User>().setQuery(query, User::class.java).build()
        ) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {

                return ContactsViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_contact, parent, false)
                )
            }

            override fun onBindViewHolder(holder: ContactsViewHolder, position: Int, model: User) {
                holder.bindMessage(model)
                holder.itemView.setOnClickListener { _ ->

                    router.navigateTo(model.login?.let { Screens.ProfileScreen(it) })
                }
            }

            override fun onChildChanged(
                type: ChangeEventType,
                snapshot: DataSnapshot,
                index: Int,
                oldIndex: Int
            ) {
                super.onChildChanged(type, snapshot, index, oldIndex)

                contactsRecycler.scrollToPosition(index)
            }
        }

        contactsRecycler.adapter = mAdapter

    }

    override fun onStop() {
        super.onStop()
        mAdapter!!.stopListening()
    }
}
