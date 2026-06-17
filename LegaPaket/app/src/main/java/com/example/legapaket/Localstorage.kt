package com.example.legapaket

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object LocalStorage {

    private const val FILE_SHIPMENTS = "shipments.json"
    private const val FILE_SESSION   = "session.json"

    private var context: Context? = null

    /** Wajib dipanggil sekali sebelum fungsi lain digunakan. */
    fun init(ctx: Context) {
        context = ctx.applicationContext
    }

    /** Cek apakah sudah di-init. */
    val isInitialized: Boolean get() = context != null

    // ─── Shipments ────────────────────────────────────────────────────────────

    fun saveShipments(list: List<ShipmentModel>) {
        val array = JSONArray()
        list.forEach { item ->
            array.put(JSONObject().apply {
                put("resi",          item.resi)
                put("senderName",    item.senderName)
                put("senderPhone",   item.senderPhone)
                put("senderAddress", item.senderAddress)
                put("receiver",      item.receiver)
                put("phone",         item.phone)
                put("address",       item.address)
                put("city",          item.city)
                put("type",          item.type)
                put("weight",        item.weight)
                put("price",         item.price)
                put("paymentMethod", item.paymentMethod)
                put("createdAt",     item.createdAt.time) // simpan sebagai Long (epoch ms)
            })
        }
        writeFile(FILE_SHIPMENTS, array.toString(2))
    }

    fun loadShipments(): List<ShipmentModel> {
        val raw = readFile(FILE_SHIPMENTS) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                ShipmentModel(
                    resi          = obj.getString("resi"),
                    senderName    = obj.optString("senderName",    ""),
                    senderPhone   = obj.optString("senderPhone",   ""),
                    senderAddress = obj.optString("senderAddress", ""),
                    receiver      = obj.getString("receiver"),
                    phone         = obj.getString("phone"),
                    address       = obj.getString("address"),
                    city          = obj.getString("city"),
                    type          = obj.getString("type"),
                    weight        = obj.getDouble("weight"),
                    price         = obj.getInt("price"),
                    paymentMethod = obj.optString("paymentMethod", ""),
                    createdAt     = java.util.Date(obj.getLong("createdAt"))
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // ─── Session (username login) ─────────────────────────────────────────────

    fun saveSession(username: String) {
        val obj = JSONObject().apply { put("username", username) }
        writeFile(FILE_SESSION, obj.toString())
    }

    fun loadSession(): String? {
        val raw = readFile(FILE_SESSION) ?: return null
        return try {
            JSONObject(raw).optString("username", null)
        } catch (e: Exception) {
            null
        }
    }

    fun clearSession() {
        deleteFile(FILE_SESSION)
    }

    // ─── Internal file helpers ────────────────────────────────────────────────

    private fun writeFile(name: String, content: String) {
        val ctx = context ?: throw IllegalStateException(
            "LocalStorage belum di-init. Pastikan android:name=\".LegaPaketApp\" sudah ada di AndroidManifest.xml"
        )
        File(ctx.filesDir, name).writeText(content, Charsets.UTF_8)
    }

    private fun readFile(name: String): String? {
        val ctx = context ?: throw IllegalStateException(
            "LocalStorage belum di-init. Pastikan android:name=\".LegaPaketApp\" sudah ada di AndroidManifest.xml"
        )
        val file = File(ctx.filesDir, name)
        return if (file.exists()) file.readText(Charsets.UTF_8) else null
    }

    private fun deleteFile(name: String) {
        val ctx = context ?: return
        File(ctx.filesDir, name).delete()
    }
}