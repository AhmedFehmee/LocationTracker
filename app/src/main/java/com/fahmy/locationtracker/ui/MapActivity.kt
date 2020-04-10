package com.fahmy.locationtracker.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.fahmy.locationtracker.R
import com.fahmy.locationtracker.utils.NetworkUtil
import kotlinx.android.synthetic.main.activity_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    var simLat: Double? = null
    var simLon: Double? = null
    var simAddress: String? = null

    var gpsLat: Double? = null
    var gpsLon: Double? = null
    private var gpsAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        NetworkUtil.setStrictMode()

        receiveIntentData()
        initMap()

        setPoints()
    }

    private fun setPoints() {
        createMarker(gpsLat!!, gpsLon!!, resources.getDrawable(R.drawable.marker), gpsAddress!!)

        createMarker(simLat!!, simLon!!, resources.getDrawable(R.drawable.tower), simAddress!!)

        map.invalidate()
    }

    private fun initMap() {
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        //map.tileProvider.clearTileCache()

        // Display the map with initial point and initial zoom
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        val startPoint = GeoPoint(gpsLat!!, gpsLon!!)
        val mapController = map.controller
        mapController.setZoom(17.0)
        mapController.setCenter(startPoint)

        Configuration.getInstance().cacheMapTileCount = 12.toShort()
        Configuration.getInstance().cacheMapTileOvershoot = 12.toShort()
        // Create a custom tile source
        map.setTileSource(object : OnlineTileSourceBase(
            "", 1, 20, 512, ".png",
            arrayOf("https://a.tile.openstreetmap.org/")
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                return (baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "/" + MapTileIndex.getX(pMapTileIndex) + "/" + MapTileIndex.getY(pMapTileIndex) + mImageFilenameEnding)
            }
        })

        map.invalidate()
    }

    private fun receiveIntentData() {
        gpsLat = intent.getDoubleExtra("gpsLat", 0.0)
        gpsLon = intent.getDoubleExtra("gpsLon", 0.0)
        gpsAddress = intent.getStringExtra("gpsAddress")

        simLat = intent.getDoubleExtra("simLat", 0.0)
        simLon = intent.getDoubleExtra("simLon", 0.0)
        simAddress = intent.getStringExtra("simAddress")
    }

    private fun createMarker(lat: Double, lon: Double, drawable: Drawable, title: String) {
        if (map == null) return

        val myMarker = Marker(map)
        myMarker.position = GeoPoint(lat, lon)
        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        myMarker.title = title
        myMarker.setPanToView(true)
        map.overlays.add(myMarker)
        myMarker.icon = drawable

        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs =
        PreferenceManager.getDefaultSharedPreferences(this)
        //Configuration.getInstance().save(this, prefs);
        map.onPause()
    }
}
