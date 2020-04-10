package com.fahmy.locationtracker.utils

import android.content.Context
import android.net.ConnectivityManager
import android.os.StrictMode

class NetworkUtil {

    companion object {
        var TYPE_WIFI = 1
        var TYPE_MOBILE = 2
        var TYPE_NOT_CONNECTED = 0

        private fun getConnectivityStatus(context: Context): Int {
            val cm = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) {
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI

                if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE
            }
            return TYPE_NOT_CONNECTED
        }

        fun getConnectivityStatusString(context: Context): Int? {
            val conn = getConnectivityStatus(context)
            var status: Int? = null
            when (conn) {
                TYPE_WIFI -> status = 0
                TYPE_MOBILE -> status = 0
                TYPE_NOT_CONNECTED -> status = 1
            }
            return status
        }

        fun setStrictMode(){
            val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }
}