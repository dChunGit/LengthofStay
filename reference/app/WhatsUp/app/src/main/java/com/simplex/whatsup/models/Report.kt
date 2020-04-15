package com.simplex.whatsup.models

import android.graphics.Bitmap

data class Report(
    val id: String,
    val username: String,
    val event_id: String,
    val comments: String,
    val time: String,
    val report_image: String?,
    val category: String = "",
    @Transient var image: Bitmap? = null
)