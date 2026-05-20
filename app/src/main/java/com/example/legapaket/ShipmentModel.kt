package com.example.legapaket

data class ShipmentModel(
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
    val paymentMethod: String = ""
)