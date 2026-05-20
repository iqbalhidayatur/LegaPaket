package com.example.legapaket

data class ReportModel(
    val resi: String,
    val receiver: String,
    val city: String,
    val status: String,
    val date: String,
    val type: String = "",
    val weight: Double = 0.0,
    val price: Int = 0,
    val paymentMethod: String = ""
)