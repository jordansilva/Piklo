package com.jordansilva.imageloader.repository.http.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

class AndroidNetwork(private val context: Context) : NetworkConnection {

    override fun isConnected(): Boolean = if (Build.VERSION.SDK_INT < 23) isConnectedLollipop() else isConnectedApi23()

    private fun isConnectedLollipop(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.run {
            isConnected && (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE)
        } ?: false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnectedApi23(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.getNetworkCapabilities(cm.activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } ?: false
    }

}