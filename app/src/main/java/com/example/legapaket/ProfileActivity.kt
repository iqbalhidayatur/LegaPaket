package com.example.legapaket

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnLogout: MaterialButton
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var tvFullName: TextView
    private lateinit var tvAgentName: TextView
    private lateinit var tvUsernameValue: TextView
    private lateinit var tvEmailValue: TextView
    private lateinit var tvPhoneValue: TextView
    private lateinit var tvAddressValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initView()
        loadUserData()
        setupNavigation()
        setupButton()
    }

    private fun initView() {
        btnLogout = findViewById(R.id.btnLogout)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        tvFullName = findViewById(R.id.tvFullName)
        tvAgentName = findViewById(R.id.tvAgentName)
        tvUsernameValue = findViewById(R.id.tvUsernameValue)
        tvEmailValue = findViewById(R.id.tvEmailValue)
        tvPhoneValue = findViewById(R.id.tvPhoneValue)
        tvAddressValue = findViewById(R.id.tvAddressValue)
    }

    private fun loadUserData() {
        val user = UserSession.currentUser ?: return

        tvFullName.text = user.fullName
        tvAgentName.text = user.agentName
        tvUsernameValue.text = user.username
        tvEmailValue.text = user.email
        tvPhoneValue.text = user.phone
        tvAddressValue.text = user.address
    }

    private fun setupButton() {
        btnLogout.setOnClickListener {
            UserSession.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupNavigation() {
        bottomNavigation.selectedItemId = R.id.menu_profile

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.menu_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }

                R.id.menu_shipment -> {
                    startActivity(Intent(this, ShipmentActivity::class.java))
                    true
                }

                R.id.menu_reports -> {
                    startActivity(Intent(this, ReportsActivity::class.java))
                    true
                }

                R.id.menu_profile -> true

                else -> false
            }
        }
    }
}