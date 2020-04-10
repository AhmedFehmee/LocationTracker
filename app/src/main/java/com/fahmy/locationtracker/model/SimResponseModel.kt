package com.fahmy.locationtracker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SimResponseModel {
    @Expose
    @SerializedName("address")
    val address: String? = null

    @Expose
    @SerializedName("accuracy")
    private val accuracy = 0

    @Expose
    @SerializedName("lon")
    val lon = 0.0

    @Expose
    @SerializedName("lat")
    val lat = 0.0

    @Expose
    @SerializedName("balance")
    private val balance = 0

    @Expose
    @SerializedName("status")
    private val status: String? = null
}