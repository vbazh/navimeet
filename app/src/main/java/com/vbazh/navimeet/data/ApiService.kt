package com.vbazh.navimeet.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {


    @GET("v1.5/Addresses/{container}/{address}?lang=ru")
    fun getAddress(@Path("container") container: String, @Path("address") address: String): Call<AddressResponse>
}