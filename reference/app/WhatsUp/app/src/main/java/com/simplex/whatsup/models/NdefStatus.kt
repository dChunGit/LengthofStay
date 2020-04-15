package com.simplex.whatsup.models

data class NdefStatus(var status: Boolean, var eventId: String) {
    fun clearStatus() {
        status = false
        eventId = ""
    }
}