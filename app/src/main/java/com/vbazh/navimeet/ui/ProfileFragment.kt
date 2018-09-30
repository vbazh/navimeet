package com.vbazh.navimeet.ui

import android.content.Context
import android.content.Intent
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
import com.vbazh.navimeet.data.Interval
import com.vbazh.navimeet.di.ComponentManager
import kotlinx.android.synthetic.main.fragment_profile.*
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class ProfileFragment : Fragment() {

    companion object {
        const val TAG = "ProfileFragment"

        const val PROFILE_ID = "profile_id"

        fun newInstance(idUser: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString(PROFILE_ID, idUser)
            fragment.arguments = args
            return fragment
        }
    }

    val TAG = "TAG"

    private var mDatabase: DatabaseReference? = null
    private var mIntervalsReference: DatabaseReference? = null
    private var mIntervalsListener: ChildEventListener? = null

    @Inject
    lateinit var router: Router

    private var mAdapter: FirebaseRecyclerAdapter<Interval, IntervalViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentManager.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mAdapter?.startListening()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    private fun firebaseListenerInit() {

        val childEventListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // A new message has been added
                // onChildAdded() will be called for each node at the first time
//                val message = dataSnapshot.getValue(Interval::class.java)

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                // A message has been removed
                val interval = dataSnapshot.getValue(Interval::class.java)

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {

                // A message has changed position
                val interval = dataSnapshot.getValue(Interval::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        mIntervalsReference!!.addChildEventListener(childEventListener)

        // copy for removing at onStop()
        mIntervalsListener = childEventListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getString(PROFILE_ID)

        username.text = userId

        mDatabase = FirebaseDatabase.getInstance().reference
        mIntervalsReference =
                userId?.let {
                    FirebaseDatabase.getInstance().getReference("users").child(it)
                        .child("intervals")
                }

        firebaseListenerInit()

        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = false
        intervalsRecycler.setHasFixedSize(true)
        intervalsRecycler.layoutManager = layoutManager

        val query = mIntervalsReference!!.limitToFirst(20)

        mAdapter = object : FirebaseRecyclerAdapter<Interval, IntervalViewHolder>(
            FirebaseRecyclerOptions.Builder<Interval>().setQuery(
                query,
                Interval::class.java
            ).build()
        ) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntervalViewHolder {

                return IntervalViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_interval, parent, false)
                )
            }

            override fun onBindViewHolder(
                holder: IntervalViewHolder,
                position: Int,
                model: Interval
            ) {
                holder.bindInterval(model)
                holder.itemView.setOnClickListener { _ ->

                    router.navigateTo(model.let {
                        userId?.let { userId ->
                            model.id?.let { intervalId ->
                                Screens.IntervalScreen(
                                    userId,
                                    intervalId
                                )
                            }
                        }
                    })
                }
            }

            override fun onChildChanged(
                type: ChangeEventType,
                snapshot: DataSnapshot,
                index: Int,
                oldIndex: Int
            ) {
                super.onChildChanged(type, snapshot, index, oldIndex)

                intervalsRecycler.scrollToPosition(index)
            }
        }

        intervalsRecycler.adapter = mAdapter

        val sharedprefs = context?.getSharedPreferences("pref_data", Context.MODE_PRIVATE)

        if (sharedprefs?.getString("user_id", "") == arguments?.getString(PROFILE_ID)) {

            addIntervalButton.visibility = View.VISIBLE
            share.visibility = View.VISIBLE
            share.setOnClickListener {

                shareProfile()
            }

        } else {

            addIntervalButton.visibility = View.GONE
            share.visibility = View.GONE
        }

    }

    fun shareProfile() {

        val link = "https://open.navimeet.app.user/${arguments?.getString(PROFILE_ID)}"

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        shareIntent.type = "text/plain"
        startActivity(shareIntent)
    }

    override fun onStop() {
        super.onStop()
        mAdapter!!.stopListening()
    }
}