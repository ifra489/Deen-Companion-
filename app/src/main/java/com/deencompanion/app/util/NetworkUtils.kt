package com.deencompanion.app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * LEARNING NOTE:
 * This class provides a utility to check if the Android device is connected to the internet.
 * It uses the system's ConnectivityManager to inspect active network transports (WiFi, Cellular, Ethernet).
 * Other components, such as repositories, audio players, or ViewModels, will call this helper to decide
 * whether to load cached offline data or fetch fresh resources online.
 */
object NetworkUtils {

    /**
     * Checks if there is an active internet connection on the device.
     * Returns true if connected via Wifi, Cellular, or Ethernet, false otherwise.
     */
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
