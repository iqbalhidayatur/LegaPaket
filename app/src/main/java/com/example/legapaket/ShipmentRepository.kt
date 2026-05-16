package com.example.legapaket

object ShipmentRepository {

    val shipmentList = mutableListOf<ShipmentModel>()

    fun add(data: ShipmentModel) {
        shipmentList.add(data)
    }

    fun getAll(): List<ShipmentModel> {
        return shipmentList
    }

    fun getTotal(): Int {
        return shipmentList.size
    }

}