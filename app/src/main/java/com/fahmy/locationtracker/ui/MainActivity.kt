package com.fahmy.locationtracker.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.fahmy.locationtracker.R
import com.fahmy.locationtracker.model.CellLocationModel
import com.fahmy.locationtracker.model.CellsEntity
import com.fahmy.locationtracker.model.SimResponseModel
import com.fahmy.locationtracker.utils.LocationHelper
import com.fahmy.locationtracker.utils.LocationTracker
import com.fahmy.locationtracker.utils.NetworkUtil
import com.fahmy.locationtracker.utils.RequestPermissions
import com.google.gson.Gson
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    var gps: LocationTracker? = null
    private val disposables = CompositeDisposable()

    private var simLat: Double? = null
    private var simLon: Double? = null
    private var simAddress: String? = null

    private var gpsLat: Double? = null
    private var gpsLon: Double? = null
    private var addressText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NetworkUtil.setStrictMode()

        handleClicks()
    }

    private fun handleClicks() {

        btnShowLocation.setOnClickListener {
            checkGpsSensor()
        }

        btn_show_on_map.setOnClickListener {
            if (simAddress != null && addressText != null) {
                val intent = Intent(this@MainActivity, MapActivity::class.java)
                intent.putExtra("gpsLat", gpsLat)
                intent.putExtra("gpsLon", gpsLon)
                intent.putExtra("gpsAddress", addressText)

                intent.putExtra("simLat", simLat)
                intent.putExtra("simLon", simLon)
                intent.putExtra("simAddress", simAddress)
                startActivity(intent)
            } else
                checkGpsSensor()
        }
    }

    private fun checkGpsSensor() {
        if (!RequestPermissions.requirePermission(this)) {
            disposables.add(
                RxPermissions(this).request(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).subscribe {
                    if (it)
                        if (!LocationHelper.isLocationEnabled(this)) {
                            LocationHelper.displayLocationSettingsRequest(this)
                        } else {
                            getLocation()
                        }
                })
        } else if (!LocationHelper.isLocationEnabled(this)) {
            LocationHelper.displayLocationSettingsRequest(this)
        } else {
            getLocation()
        }
    }

    private fun getLocation() {
        // create class object
        gps = LocationTracker(this)
        gps?.getCurrentLocation()

        getLocationFromGPS(gps!!)
        getLocationWithSimCard()
    }

    private fun getLocationFromGPS(gps: LocationTracker) {
        if (LocationHelper.isLocationEnabled(this)) {
            // check if GPS enabled
            if (gps.canGetLocation()) {

                gpsLat = gps.getDeviceLatitude()
                gpsLon = gps.getDeviceLongitude()

                addressText = getAddressFromLocation(gpsLat!!, gpsLon!!)
            }
        }
    }

    private fun getAddressFromLocation(lat: Double, lon: Double): String? {
        val gcd = Geocoder(applicationContext, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = gcd.getFromLocation(lat, lon, 1)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                println(addresses[0].locality)

                val address = addresses[0]

                tv_location.text = "Your Location is: ${String.format("%s", address.getAddressLine(0))}"

                return String.format("%s", address.getAddressLine(0))
            } else {
                // Ask user to enable GPS/network in settings
                LocationHelper.displayLocationSettingsRequest(this)
            }
        } else {
            // Ask user to enable GPS/network in settings
            LocationHelper.displayLocationSettingsRequest(this)
            return null
        }
        return null
    }

    private fun getLocationWithSimCard() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)

        if (permission != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), 1)
        else {

            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val cellLocation = telephonyManager.allCellInfo
            val networkOperator: String = telephonyManager.networkOperator

            val cellLocation2: GsmCellLocation? = telephonyManager.cellLocation as? GsmCellLocation

            var mcc = 0
            var mnc = 0
            var lac: Int? = null
            var cid: Int? = null
            if (!TextUtils.isEmpty(networkOperator)) {
                mcc = networkOperator.substring(0, 3).toInt()
                mnc = networkOperator.substring(3).toInt()
            }
            if (cellLocation != null) {
                lac = cellLocation2?.lac
                cid = cellLocation2?.cid
            }
            sendGetRequest(mcc, mnc, lac ?: 0, cid ?: 0)
        }
    }

    private fun sendGetRequest(mcc: Int, mnc: Int, lac: Int, cid: Int) {
        if (NetworkUtil.getConnectivityStatusString(this) == 0) {
            val executor: Executor = Executors.newSingleThreadExecutor()
            executor.execute {
                val cellModel = CellLocationModel()
                cellModel.mcc = mcc
                cellModel.mnc = mnc
                val locationData = arrayListOf<CellsEntity>()
                locationData.add(CellsEntity(lac, cid, 0))
                cellModel.cells = locationData

                val finalURL = URL("https://us1.unwiredlabs.com/v2/process.php")

                with(finalURL.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"

                    val wr = OutputStreamWriter(outputStream)
                    wr.write(Gson().toJson(cellModel))
                    wr.flush()

                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        val simResponse = Gson().fromJson(response.toString(), SimResponseModel::class.java)

                        simLat = simResponse.lat
                        simLon = simResponse.lon
                        simAddress = simResponse.address
                        runOnUiThread {
                            tv_sim_location.text = "Your tower Location is: $simAddress"
                        }
                    }
                }
            }
        } else {
            MaterialDialog.Builder(this)
                .title(getString(R.string.whoops))
                .content(getString(R.string.no_internet_connection))
                .positiveText(getString(R.string.connect_to_internet))
                .negativeText(getString(R.string.cancel))
                .cancelable(true)
                .contentGravity(GravityEnum.CENTER)
                .btnStackedGravity(GravityEnum.CENTER)
                .itemsGravity(GravityEnum.CENTER)
                .onPositive { _, _ ->
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
                .onNegative { _, _ -> }
                .iconRes(R.drawable.wifi_disabled)
                .maxIconSize(95)
        }
    }
}
