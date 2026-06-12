package com.example.legapaket

import java.util.Date

data class ActivityModel(
    val resi: String,
    val destination: String,
    val status: String,
    val time: String,
    val createdAt: Date = Date(),
    val type: String = "",
    val weight: Double = 0.0,
    val price: Int = 0,
    val paymentMethod: String = ""
)