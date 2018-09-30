package com.vbazh.navimeet.data

import com.google.gson.annotations.SerializedName

data class AddressResponse(

    @SerializedName("result") val result: Result
)

data class Result(

    @SerializedName("point") val point: Point
)

data class Point(

    @SerializedName("lat") val latitude: Double,
    @SerializedName("lng") val longitude: Double
)