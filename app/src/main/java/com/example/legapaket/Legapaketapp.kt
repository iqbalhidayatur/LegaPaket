package com.example.legapaket

import android.app.Application

class LegaPaketApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Init LocalStorage dengan applicationContext
        LocalStorage.init(this)

        // 2. Load (atau seed pertama kali) data shipment dari JSON
        ShipmentRepository.initialize()
    }
}