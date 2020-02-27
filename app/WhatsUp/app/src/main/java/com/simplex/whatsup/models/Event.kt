package com.simplex.whatsup.models

import android.graphics.Bitmap

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val location: String,
    val geo_latitude: String,
    val geo_longitude: String,
    val status: Boolean,
    val last_updated_user: String,
    val first_update_time: String,
    val last_update_time: String,
    val cover_image: String?,
    val reports: List<String>,
    val category: String = "",
    @Transient var image: Bitmap? = null
) {
    fun toNDefString() = "$id::$title::$description::$time::$location"
}