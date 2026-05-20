package com.example.legapaket

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvTotalMasuk: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var btnInputShipment: MaterialButton
    private lateinit var btnReports: MaterialButton
    private lateinit var recyclerActivity: RecyclerView

    private lateinit var txtToolbarGreeting: TextView
    private lateinit var txtToolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        recyclerActivity = findViewById(R.id.recyclerActivity)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        btnInputShipment = findViewById(R.id.btnInputShipment)
        btnReports = findViewById(R.id.btnReports)
        tvTotalMasuk = findViewById(R.id.tvTotalMasuk)

        txtToolbarGreeting = findViewById(R.id.txtToolbarGreeting)
        txtToolbarTitle = findViewById(R.id.txtToolbarTitle)

        setupToolbar()
        setupNavigation()
        setupButton()
        loadData()
        updateDashboard()
    }

    private fun setupToolbar() {
        val user = UserSession.currentUser
        if (user != null) {
            txtToolbarGreeting.text = "Selamat datang,"
            txtToolbarTitle.text = user.fullName
        }
    }

    private fun updateDashboard() {
        tvTotalMasuk.text = ShipmentRepository.getTotal().toString()
    }

    private fun setupNavigation() {

        bottomNavigation.selectedItemId = R.id.menu_dashboard

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.menu_dashboard -> true

                R.id.menu_shipment -> {
                    startActivity(Intent(this, ShipmentActivity::class.java))
                    true
                }

                R.id.menu_reports -> {
                    startActivity(Intent(this, ReportsActivity::class.java))
                    true
                }

                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun loadData() {
        val activityList = ShipmentRepository.getAll().map { item ->
            ActivityModel(
                resi          = "LP-${item.receiver.take(4).uppercase()}",
                destination   = "Tujuan: ${item.city} - ${item.receiver}",
                status        = "TERKIRIM",
                time          = "Baru saja",
                type          = item.type,           // ← isi dari ShipmentModel
                weight        = item.weight,         // ← isi dari ShipmentModel
                price         = item.price,          // ← isi dari ShipmentModel
                paymentMethod = item.paymentMethod   // ← isi dari ShipmentModel
            )
        }

        recyclerActivity.layoutManager = LinearLayoutManager(this)
        recyclerActivity.adapter = ActivityAdapter(activityList)
    }

    private fun setupButton() {

        btnInputShipment.setOnClickListener {
            startActivity(Intent(this, ShipmentActivity::class.java))
        }

        btnReports.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
        updateDashboard()
    }
}