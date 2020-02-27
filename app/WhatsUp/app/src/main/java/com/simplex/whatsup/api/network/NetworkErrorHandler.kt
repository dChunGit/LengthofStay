package com.simplex.whatsup.api.network

import android.content.Context
import android.widget.Toast

class NetworkErrorHandler(private val context: Context) {
    fun handleNetworkError() {
        Toast.makeText(context, "Network sync failed, please try again later", Toast.LENGTH_SHORT).show()
    }
}