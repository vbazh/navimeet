package com.vbazh.navimeet.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.database.*
import com.vbazh.navimeet.R
import com.vbazh.navimeet.data.Interval
import com.vbazh.navimeet.di.ComponentManager
import kotlinx.android.synthetic.main.fragment_interval.*
import ru.terrakok.cicerone.Router
import javax.inject.Inject
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.vbazh.navimeet.data.AddressResponse
import com.vbazh.navimeet.data.ApiService
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.callback.Callback


class IntervalFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val TAG = "IntervalFragment"

        const val PROFILE_ID = "profile_id"
        const val INTERVAL_ID = "interval_id"

        fun newInstance(idUser: String, idInterval: Int): IntervalFragment {
            val fragment = IntervalFragment()
            val args = Bundle()
            args.putString(PROFILE_ID, idUser)
            args.putInt(INTERVAL_ID, idInterval)
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var apiService: ApiService

    var googleMap: GoogleMap? = null

    val database = FirebaseDatabase.getInstance()
    var ref: DatabaseReference? = null

    var container: String? = null
    var address: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentManager.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_interval, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment

        mapFragment?.getMapAsync(this)

        ref = arguments?.getString(PROFILE_ID)?.let { profileId ->
            username.text = profileId

            database
                .getReference("users").child(profileId)
                .child("intervals").child(arguments?.getInt(INTERVAL_ID).toString())
        }

        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val interval = dataSnapshot.getValue(Interval::class.java)

                if (interval?.container == null && interval?.address == null) {
                    return
                }

                container = interval.container
                address = interval.address

                addressText.setOnClickListener { openLink(container!!, address!!) }

                val startDate =
                    SimpleDateFormat("yyyy.MM.dd ',' HH:mm").format(Date(interval.start!!))
                val endDate = SimpleDateFormat("yyyy.MM.dd ',' HH:mm").format(Date(interval.end!!))

                timeInterval.text = "$startDate - $endDate"

                addressText.text = "[${interval?.container}] ${interval?.address}"

                descriptionText.text = interval.description.toString()

                apiService.getAddress(interval.container!!, interval.address!!)
                    .enqueue(object : Callback,
                        retrofit2.Callback<AddressResponse> {
                        override fun onFailure(call: Call<AddressResponse>, t: Throwable) {

                        }

                        override fun onResponse(
                            call: Call<AddressResponse>,
                            response: Response<AddressResponse>
                        ) {

                            if (response.isSuccessful) {

                                val point = response.body()?.result?.point

                                point?.latitude?.let { addMarker(it, point.longitude) }
                            }
                        }
                    })
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        this.googleMap = googleMap
    }

    fun openLink(container: String, address: String) {

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://naviaddress.com/$container/$address")
        startActivity(intent)
    }

    fun addMarker(latitude: Double, longitude: Double) {

        val sydney = LatLng(latitude, longitude)
        googleMap?.addMarker(
            MarkerOptions().position(sydney)
        )

        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))
    }
}