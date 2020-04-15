package com.simplex.whatsup.models

data class User(
    val id: String,
    val email: String,
    val name: String,
    val karma: Int,
    val subscribed_events: List<String>,
    val reports: List<String>
)