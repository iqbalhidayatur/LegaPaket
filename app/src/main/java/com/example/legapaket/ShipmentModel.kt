package com.example.legapaket

import java.util.Date

data class ShipmentModel(

    val resi: String = "",

    val senderName: String = "",
    val senderPhone: String = "",
    val senderAddress: String = "",

    val receiver: String,
    val phone: String,
    val address: String,
    val city: String,
    val type: String,
    val weight: Double,
    val price: Int,
    val paymentMethod: String = "",

    // Timestamp otomatis saat data dibuat
    val createdAt: Date = Date()
)