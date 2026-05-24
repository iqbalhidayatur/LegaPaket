package com.example.legapaket

object ShipmentRepository {

    val shipmentList = mutableListOf(

        ShipmentModel(
            resi = "LP-JKT-220526-381",
            senderName = "Andi",
            senderPhone = "081234567890",
            senderAddress = "Bandung",
            receiver = "Budi",
            phone = "089876543210",
            address = "Jl Mawar No 10",
            city = "Jakarta",
            type = "Express",
            weight = 2.5,
            price = 35000,
            paymentMethod = "GoPay"
        ),

        ShipmentModel(
            resi = "LP-BDG-220526-742",
            senderName = "Rina",
            senderPhone = "081298765432",
            senderAddress = "Cimahi",
            receiver = "Salsa",
            phone = "082112223333",
            address = "Jl Melati No 8",
            city = "Bandung",
            type = "Regular",
            weight = 1.2,
            price = 22000,
            paymentMethod = "DANA"
        ),

        ShipmentModel(
            resi = "LP-SBY-220526-115",
            senderName = "Fajar",
            senderPhone = "081377788899",
            senderAddress = "Garut",
            receiver = "Tono",
            phone = "081455566677",
            address = "Jl Anggrek No 5",
            city = "Surabaya",
            type = "Cargo",
            weight = 7.8,
            price = 85000,
            paymentMethod = "ShopeePay"
        ),

        ShipmentModel(
            resi = "LP-MKS-220526-928",
            senderName = "Dewi",
            senderPhone = "081355577788",
            senderAddress = "Tasikmalaya",
            receiver = "Rudi",
            phone = "082233344455",
            address = "Jl Kenanga No 3",
            city = "Makassar",
            type = "Same Day",
            weight = 3.4,
            price = 47000,
            paymentMethod = "QRIS"
        )
    )

    fun add(data: ShipmentModel) {
        shipmentList.add(data)
    }

    fun getAll(): List<ShipmentModel> {
        return shipmentList.toList()
    }

    fun getTotal(): Int {
        return shipmentList.size
    }

    fun update(index: Int, data: ShipmentModel) {
        if (index in shipmentList.indices) {
            shipmentList[index] = data
        }
    }

    fun delete(index: Int) {
        if (index in shipmentList.indices) {
            shipmentList.removeAt(index)
        }
    }
}