package com.fahmy.locationtracker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log

class LocationTracker(private val mContext: Context) : LocationListener {

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
    protected var locationManager: LocationManager? = null

    /**
     * Function to get the user's current location
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): Location? {
        try {
            locationManager = mContext
                ?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // getting GPS status
            isGPSEnabled = locationManager
                ?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!

            //Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                ?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)!!

            //Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true
                if (isNetworkEnabled) {
                    deviceLocation = null
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    //Log.d("Network", "Network");
                    if (locationManager != null) {
                        deviceLocation = locationManager
                            ?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (deviceLocation != null) {
                            latitude = deviceLocation?.latitude!!
                            longitude = deviceLocation?.longitude!!
                            //Log.i("NetworkCurrent",latitude+"");
                            //Log.i("NetworkCurrent",longitude+"");
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    deviceLocation = null
                    if (deviceLocation == null) {
                        locationManager?.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        //Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            deviceLocation = locationManager
                                ?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (deviceLocation != null) {
                                latitude = deviceLocation?.latitude!!
                                longitude = deviceLocation?.longitude!!
                                //Log.i("GpsCurrent",latitude+"");
                                //Log.i("GPSCurrent",longitude+"");
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return deviceLocation
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    fun stopUsingGPS() {
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

        // return latitude
        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getDeviceLongitude(): Double {
        if (deviceLocation != null) {
            longitude = deviceLocation?.longitude!!
        }

        // return longitude
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
        this.canGetLocation = true
        if (isNetworkEnabled) {
            deviceLocation = null
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
            )
            //Log.d("Network", "Network");
            if (locationManager != null) {
                deviceLocation =
                    locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (deviceLocation != null) {
                    latitude = location?.latitude!!
                    longitude = location?.longitude!!
                    Log.i("NetworkUpdate", "" + latitude)
                    Log.i("NetworkUpdate", "" + longitude)
                }
            }
        }
        if (isGPSEnabled) {
            deviceLocation = null
            if (location == null) {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                )
                //Log.d("GPS Enabled", "GPS Enabled");
                if (locationManager != null) {
                    deviceLocation =
                        locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (deviceLocation != null) {
                        latitude = location?.latitude!!
                        longitude = location?.longitude!!
                        Log.i("GPSUpdate", "" + latitude)
                        //Log.i("GPSUpdate",longitude+"");
                    }
                }
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}