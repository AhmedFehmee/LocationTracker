package com.fahmy.locationtracker.utils

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import timber.log.Timber


open class LocationHelper(private val context: Context) {

    companion object {
        fun isLocationEnabled(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            return gpsEnabled || networkEnabled
        }

        fun displayLocationSettingsRequest(activity: Activity) {
            val googleApiClient = GoogleApiClient.Builder(activity)
                    .addApi(LocationServices.API).build()
            googleApiClient.connect()
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 10000
            locationRequest.fastestInterval = 10000 / 2
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)
            val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
            result.setResultCallback { result ->
                val status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> Timber.i("All location settings are satisfied.")
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Timber.i("Location settings are not satisfied. Show the user a dialog to upgrade location settings ")
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(activity, 0x1)
                        } catch (e: IntentSender.SendIntentException) {
                            Timber.i("PendingIntent unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Timber.i("Location settings are inadequate, and cannot be fixed here. Dialog not created.")
                }
            }
        }
    }
}