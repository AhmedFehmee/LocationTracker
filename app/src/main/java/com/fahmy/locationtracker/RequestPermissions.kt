package com.fahmy.locationtracker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import com.tbruyelle.rxpermissions2.RxPermissions

class RequestPermissions {

    companion object {

        fun isLocationEnabled(context: Context): Boolean {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            return gpsEnabled || networkEnabled
        }

        fun requirePermission(context: Activity): Boolean {

            val permissions = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE
            )
            for (permission in permissions) {
                val granted = RxPermissions(context).isGranted(permission)
                if (!granted)
                    return false
            }
            return true
        }
    }
}