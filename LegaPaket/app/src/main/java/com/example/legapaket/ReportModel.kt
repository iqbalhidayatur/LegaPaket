package com.example.legapaket

import java.util.Date

data class ReportModel(
    val resi: String,
    val receiver: String,
    val city: String,
    val status: String,
    val date: String,           // Tampilan tanggal & jam yang sudah diformat
    val createdAt: Date,        // Timestamp asli untuk sorting & filter
    val type: String = "",
    val weight: Double = 0.0,
    val price: Int = 0,
    val paymentMethod: String = ""
)