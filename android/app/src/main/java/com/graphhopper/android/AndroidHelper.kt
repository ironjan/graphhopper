package com.graphhopper.android

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

import java.io.BufferedReader
import java.io.IOException
import java.io.Reader
import java.util.ArrayList

object AndroidHelper {
    @Throws(IOException::class)
    fun readFile(simpleReader: Reader): List<String> {
        val reader = BufferedReader(simpleReader)
        try {
            val res = ArrayList<String>()
            var line: String? = reader.readLine()
            while (line != null) {
                res.add(line)
                line = reader.readLine()
            }
            return res
        } finally {
            reader.close()
        }
    }

    fun isFastDownload(ctx: Context): Boolean {
        val mgrConn = ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return mgrConn.activeNetworkInfo != null && mgrConn.activeNetworkInfo!!.state == NetworkInfo.State.CONNECTED
        // TelephonyManager mgrTel = (TelephonyManager)
        // ctx.getSystemService(Context.TELEPHONY_SERVICE);
        // || mgrTel.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS) {
    }

    fun getFileName(str: String): String {
        val index = str.lastIndexOf("/")
        return if (index > 0) {
            str.substring(index + 1)
        } else str
    }
}
