package com.example.legapaket

import java.util.Calendar
import java.util.Date

object ShipmentRepository {

    private val shipmentList: MutableList<ShipmentModel> = mutableListOf()

    private var loaded = false

    private fun daysAgo(days: Int, hour: Int = 9, minute: Int = 0): Date {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }.time
    }

    // ─── Seed data awal (hanya dipakai saat file JSON belum ada) ─────────────

    private fun seedData(): List<ShipmentModel> = listOf(
        ShipmentModel(
            resi = "LP-JKT-220526-381", senderName = "Andi", senderPhone = "081234567890",
            senderAddress = "Bandung", receiver = "Budi", phone = "089876543210",
            address = "Jl Mawar No 10", city = "Jakarta", type = "Express",
            weight = 2.5, price = 35000, paymentMethod = "GoPay", createdAt = daysAgo(0, 8, 30)
        ),
        ShipmentModel(
            resi = "LP-BDG-220526-742", senderName = "Rina", senderPhone = "081298765432",
            senderAddress = "Cimahi", receiver = "Salsa", phone = "082112223333",
            address = "Jl Melati No 8", city = "Bandung", type = "Regular",
            weight = 1.2, price = 22000, paymentMethod = "DANA", createdAt = daysAgo(0, 10, 15)
        ),
        ShipmentModel(
            resi = "LP-SBY-220526-115", senderName = "Fajar", senderPhone = "081377788899",
            senderAddress = "Garut", receiver = "Tono", phone = "081455566677",
            address = "Jl Anggrek No 5", city = "Surabaya", type = "Cargo",
            weight = 7.8, price = 85000, paymentMethod = "ShopeePay", createdAt = daysAgo(0, 11, 45)
        ),
        ShipmentModel(
            resi = "LP-JKT-220526-654", senderName = "Bayu", senderPhone = "081211110002",
            senderAddress = "Bekasi", receiver = "Arif", phone = "082211110002",
            address = "Jl Flamboyan No 7", city = "Jakarta", type = "Express",
            weight = 2.2, price = 38000, paymentMethod = "GoPay", createdAt = daysAgo(0, 13, 20)
        ),
        ShipmentModel(
            resi = "LP-BDG-220526-912", senderName = "Citra", senderPhone = "081211110003",
            senderAddress = "Bandung", receiver = "Lina", phone = "082211110003",
            address = "Jl Mawar No 12", city = "Bandung", type = "Same Day",
            weight = 0.8, price = 42000, paymentMethod = "DANA", createdAt = daysAgo(0, 14, 0)
        ),
        ShipmentModel(
            resi = "LP-SBY-220526-128", senderName = "Doni", senderPhone = "081211110004",
            senderAddress = "Surabaya", receiver = "Rama", phone = "082211110004",
            address = "Jl Melati No 15", city = "Surabaya", type = "Cargo",
            weight = 8.3, price = 93000, paymentMethod = "QRIS", createdAt = daysAgo(1, 9, 0)
        ),
        ShipmentModel(
            resi = "LP-MDN-220526-774", senderName = "Eka", senderPhone = "081211110005",
            senderAddress = "Medan", receiver = "Sinta", phone = "082211110005",
            address = "Jl Anggrek No 21", city = "Medan", type = "Regular",
            weight = 3.1, price = 34000, paymentMethod = "QRIS", createdAt = daysAgo(1, 10, 30)
        ),
        ShipmentModel(
            resi = "LP-JKT-220526-891", senderName = "Farhan", senderPhone = "081211110006",
            senderAddress = "Padang", receiver = "Bima", phone = "082211110006",
            address = "Jl Cempaka No 4", city = "Jakarta", type = "Express",
            weight = 1.9, price = 39000, paymentMethod = "ShopeePay", createdAt = daysAgo(1, 14, 15)
        ),
        ShipmentModel(
            resi = "LP-BDG-220526-230", senderName = "Hendra", senderPhone = "081211110008",
            senderAddress = "Semarang", receiver = "Rika", phone = "082211110008",
            address = "Jl Sakura No 6", city = "Bandung", type = "Regular",
            weight = 1.3, price = 21000, paymentMethod = "GoPay", createdAt = daysAgo(2, 8, 0)
        ),
        ShipmentModel(
            resi = "LP-SBY-220526-761", senderName = "Indra", senderPhone = "081211110009",
            senderAddress = "Bekasi", receiver = "Dimas", phone = "082211110009",
            address = "Jl Teratai No 11", city = "Surabaya", type = "Express",
            weight = 4.1, price = 45000, paymentMethod = "DANA", createdAt = daysAgo(2, 11, 0)
        ),
        ShipmentModel(
            resi = "LP-MDN-220526-591", senderName = "Joko", senderPhone = "081211110010",
            senderAddress = "Tangerang", receiver = "Vina", phone = "082211110010",
            address = "Jl Kenari No 19", city = "Medan", type = "Cargo",
            weight = 10.5, price = 120000, paymentMethod = "QRIS", createdAt = daysAgo(2, 15, 30)
        ),
        ShipmentModel(
            resi = "LP-MKS-220526-824", senderName = "Kiki", senderPhone = "081211110011",
            senderAddress = "Palembang", receiver = "Roni", phone = "082211110011",
            address = "Jl Cemara No 13", city = "Makassar", type = "Regular",
            weight = 2.0, price = 30000, paymentMethod = "QRIS", createdAt = daysAgo(3, 9, 45)
        ),
        ShipmentModel(
            resi = "LP-MKS-220526-417", senderName = "Lukman", senderPhone = "081211110012",
            senderAddress = "Makassar", receiver = "Mila", phone = "082211110012",
            address = "Jl Bougenville No 9", city = "Makassar", type = "Express",
            weight = 3.8, price = 60000, paymentMethod = "ShopeePay", createdAt = daysAgo(3, 13, 0)
        ),
        ShipmentModel(
            resi = "LP-JKT-220526-672", senderName = "Mega", senderPhone = "081211110013",
            senderAddress = "Jambi", receiver = "Yoga", phone = "082211110013",
            address = "Jl Kamboja No 2", city = "Jakarta", type = "Same Day",
            weight = 1.1, price = 43000, paymentMethod = "GoPay", createdAt = daysAgo(4, 10, 0)
        ),
        ShipmentModel(
            resi = "LP-BDG-220526-504", senderName = "Nanda", senderPhone = "081211110014",
            senderAddress = "Bogor", receiver = "Tari", phone = "082211110014",
            address = "Jl Nusa Indah No 18", city = "Bandung", type = "Regular",
            weight = 2.9, price = 31000, paymentMethod = "DANA", createdAt = daysAgo(4, 14, 30)
        ),
        ShipmentModel(
            resi = "LP-SBY-220526-346", senderName = "Oki", senderPhone = "081211110015",
            senderAddress = "Solo", receiver = "Rizky", phone = "082211110015",
            address = "Jl Wijaya No 14", city = "Surabaya", type = "Express",
            weight = 5.4, price = 57000, paymentMethod = "GoPay", createdAt = daysAgo(5, 8, 20)
        ),
        ShipmentModel(
            resi = "LP-MDN-220526-911", senderName = "Putri", senderPhone = "081211110016",
            senderAddress = "Malang", receiver = "Kevin", phone = "082211110016",
            address = "Jl Anyelir No 20", city = "Medan", type = "Cargo",
            weight = 12.2, price = 140000, paymentMethod = "QRIS", createdAt = daysAgo(5, 11, 0)
        ),
        ShipmentModel(
            resi = "LP-MKS-220526-119", senderName = "Qori", senderPhone = "081211110017",
            senderAddress = "Lampung", receiver = "Dedi", phone = "082211110017",
            address = "Jl Rajawali No 22", city = "Makassar", type = "Regular",
            weight = 1.7, price = 27000, paymentMethod = "QRIS", createdAt = daysAgo(6, 9, 30)
        ),
        ShipmentModel(
            resi = "LP-JKT-220526-738", senderName = "Raka", senderPhone = "081211110018",
            senderAddress = "Pekanbaru", receiver = "Fitri", phone = "082211110018",
            address = "Jl Elang No 16", city = "Jakarta", type = "Same Day",
            weight = 2.4, price = 49000, paymentMethod = "ShopeePay", createdAt = daysAgo(6, 13, 45)
        ),
        ShipmentModel(
            resi = "LP-BDG-220526-286", senderName = "Sari", senderPhone = "081211110019",
            senderAddress = "Cirebon", receiver = "Ilham", phone = "082211110019",
            address = "Jl Garuda No 5", city = "Bandung", type = "Express",
            weight = 3.6, price = 52000, paymentMethod = "GoPay", createdAt = daysAgo(7, 8, 0)
        ),
        ShipmentModel(
            resi = "LP-SBY-220526-663", senderName = "Tono", senderPhone = "081211110020",
            senderAddress = "Tasikmalaya", receiver = "Nadia", phone = "082211110020",
            address = "Jl Merak No 30", city = "Surabaya", type = "Regular",
            weight = 0.9, price = 19000, paymentMethod = "DANA", createdAt = daysAgo(7, 10, 15)
        ),
        ShipmentModel(
            resi = "LP-MKS-220526-928", senderName = "Dewi", senderPhone = "081355577788",
            senderAddress = "Tasikmalaya", receiver = "Rudi", phone = "082233344455",
            address = "Jl Kenanga No 3", city = "Makassar", type = "Same Day",
            weight = 3.4, price = 47000, paymentMethod = "QRIS", createdAt = daysAgo(8, 14, 0)
        ),
        ShipmentModel(
            resi = "LP-MDN-220526-345", senderName = "Agus", senderPhone = "081211110001",
            senderAddress = "Yogyakarta", receiver = "Nina", phone = "082211110001",
            address = "Jl Merpati No 1", city = "Medan", type = "Regular",
            weight = 1.5, price = 25000, paymentMethod = "GoPay", createdAt = daysAgo(9, 9, 0)
        )
    )

    fun initialize() {
        if (loaded) return
        val fromFile = LocalStorage.loadShipments()
        if (fromFile.isEmpty()) {
            // Pertama kali install: isi seed data & langsung simpan ke file
            shipmentList.addAll(seedData())
            persist()
        } else {
            shipmentList.addAll(fromFile)
        }
        loaded = true
    }

    fun add(data: ShipmentModel) {
        shipmentList.add(data)
        persist()
    }

    fun getAll(): List<ShipmentModel> = shipmentList.toList()

    fun getTotal(): Int = shipmentList.size

    fun update(index: Int, data: ShipmentModel) {
        if (index in shipmentList.indices) {
            shipmentList[index] = data
            persist()
        }
    }

    fun delete(index: Int) {
        if (index in shipmentList.indices) {
            shipmentList.removeAt(index)
            persist()
        }
    }

    private fun persist() {
        LocalStorage.saveShipments(shipmentList)
    }
}