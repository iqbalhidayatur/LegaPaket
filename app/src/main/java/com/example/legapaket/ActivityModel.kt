package com.example.legapaket

data class ActivityModel(

    val resi: String,
    val destination: String,
    val status: String,
    val time: String,
    val type: String = "",          // ← TAMBAHKAN
    val weight: Double = 0.0,       // ← TAMBAHKAN
    val price: Int = 0,             // ← TAMBAHKAN
    val paymentMethod: String = ""  // ← TAMBAHKAN

)