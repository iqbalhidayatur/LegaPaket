package com.example.legapaket

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ReportsActivity : AppCompatActivity() {

    private lateinit var rvReports: RecyclerView
    private lateinit var adapter: ReportAdapter

    private lateinit var tvTotal: TextView
    private lateinit var tvDelivered: TextView
    private lateinit var tvPending: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        rvReports = findViewById(R.id.rvReports)
        tvTotal = findViewById(R.id.tvTotalPackage)
        tvDelivered = findViewById(R.id.tvDelivered)
        tvPending = findViewById(R.id.tvPending)

        rvReports.layoutManager = LinearLayoutManager(this)

        val data = loadData()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.selectedItemId = R.id.menu_reports

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.menu_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }

                R.id.menu_shipment -> {
                    startActivity(Intent(this, ShipmentActivity::class.java))
                    true
                }

                R.id.menu_reports -> {
                    true
                }

                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                else -> false
            }

        }

        adapter = ReportAdapter(data)
        rvReports.adapter = adapter

        updateSummary(data)
    }

    private fun updateSummary(list: List<ReportModel>) {

        tvTotal.text = list.size.toString()

        tvDelivered.text = list.count { it.status == "Delivered" }.toString()

        tvPending.text = list.count { it.status == "Processing" }.toString()

    }

    private fun loadData(): List<ReportModel> {

        return ShipmentRepository.getAll().map {

            ReportModel(
                resi = "LP-${System.currentTimeMillis()}",
                receiver = it.receiver,
                city = it.city,
                status = "Processing",
                date = "Hari ini"
            )
        }

    }

}