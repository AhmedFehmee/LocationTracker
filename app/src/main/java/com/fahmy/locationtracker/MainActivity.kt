package com.fahmy.locationtracker

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    // GPSTracker class
    var gps: LocationTracker? = null
    var wifiManager: WifiManager? = null
    var lm: LocationManager? = null
    var web: WebView? = null
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        btn_gps.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!RequestPermissions.requirePermission(this))
                disposables.add(
                    RxPermissions(this).request(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ).subscribe { granted ->
                        btn_gps.isChecked = granted
                    })
        }

        //wifi toggle
        btn_wifi.setOnCheckedChangeListener { buttonView, isChecked ->
            wifiManager?.isWifiEnabled = isChecked
        }

        // show location button click event
        btnShowLocation.setOnClickListener {
            // create class object
            gps = LocationTracker(this)

            Log.i("/////", "///////////////// " + gps?.canGetLocation)
            // check if GPS enabled
            gps?.getCurrentLocation()
            if (gps?.canGetLocation()!!) {

                val latitude = gps?.getDeviceLatitude()
                val longitude = gps?.getDeviceLongitude()

                val gcd = Geocoder(applicationContext, Locale.getDefault())
                var addresses: List<Address>? = null
                try {
                    addresses = gcd.getFromLocation(latitude!!, longitude!!, 1)
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }

                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        println(addresses[0].locality)

                        val address = addresses[0]

                        val addressText = String.format(
                            "%s, %s, %s, %s, %s, %s",
                            address.getAddressLine(0),
                            address.getAddressLine(1),
                            address.getAddressLine(2),
                            address.getAddressLine(3),
                            address.phone,
                            address.premises
                        )

                        Log.i("/////", "/////////////" + addressText)

                        // \n is for new line
                        Toast.makeText(
                            applicationContext,
                            "Your Location is - \nLat: $latitude\nLong: $longitude",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        RequestPermissions.isLocationEnabled(applicationContext)
                    }
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    RequestPermissions.isLocationEnabled(applicationContext)
                }
            }
        }
    }
}
