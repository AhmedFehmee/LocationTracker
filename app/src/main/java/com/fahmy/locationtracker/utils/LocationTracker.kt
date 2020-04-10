package com.fahmy.locationtracker.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat

open class LocationTracker(private val mContext: Context) : LocationListener {

    // flag for GPS status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    // flag for GPS status
    var canGetLocation = false

    var deviceLocation: Location? = null // location
    var latitude: Double = 0.toDouble() // latitude
    var longitude: Double = 0.toDouble() // longitude

    // The minimum distance to change Updates in meters
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // 10 meters

    // The minimum time between updates in milliseconds
    private val MIN_TIME_BW_UPDATES: Long = 1 // 1 minute

    // Declaring a Location Manager
    private var locationManager: LocationManager? = null

    /**
     * Function to get the user's current location
     *
     * @return
     */
    fun getCurrentLocation(): Location? {
        try {
            locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager

            // getting GPS status
            isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!

            // getting network status
            isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)!!

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                return null
            } else {
                getLocation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return deviceLocation
    }

    private fun getLocation() {
        this.canGetLocation = true

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                deviceLocation = null
                if (deviceLocation == null) {
                    locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                    if (locationManager != null) {
                        deviceLocation = getLastKnownLocation()
                        if (deviceLocation != null) {
                            latitude = deviceLocation?.latitude!!
                            longitude = deviceLocation?.longitude!!
                        }
                    }
                }
            } else if (isNetworkEnabled) {
                deviceLocation = null
                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                if (locationManager != null) {
                    deviceLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (deviceLocation != null) {
                        latitude = deviceLocation?.latitude!!
                        longitude = deviceLocation?.longitude!!
                    }
                }
            }
        }
    }

    /**
     * Getting the Best Location with GPS
     */
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers: List<String> = locationManager?.getProviders(true) as List<String>
        var bestLocation: Location? = null
        for (provider in providers) {
            val l: Location = locationManager?.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) { // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    private fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager?.removeUpdates(this)
        }
    }

    /**
     * Function to get latitude
     */
    fun getDeviceLatitude(): Double {
        if (deviceLocation != null) {
            latitude = deviceLocation?.latitude!!
        }
        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getDeviceLongitude(): Double {
        if (deviceLocation != null) {
            longitude = deviceLocation?.longitude!!
        }
        return longitude
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }

    @SuppressLint("MissingPermission")
    override fun onLocationChanged(location: Location?) {
        getLocation()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        stopUsingGPS()
    }
}