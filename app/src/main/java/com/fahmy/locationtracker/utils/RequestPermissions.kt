package com.fahmy.locationtracker.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import com.tbruyelle.rxpermissions2.RxPermissions

class RequestPermissions {

    companion object {

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