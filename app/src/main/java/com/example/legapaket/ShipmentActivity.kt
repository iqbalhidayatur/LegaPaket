package com.example.legapaket

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class ShipmentActivity : AppCompatActivity() {

    private lateinit var spinnerType: Spinner
    private lateinit var spinnerCity: Spinner

    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton

    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var edtWeight: EditText
    private lateinit var edtLength: EditText
    private lateinit var edtWidth: EditText
    private lateinit var edtHeight: EditText
    private lateinit var edtPrice: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipment)

        initView()
        setupSpinner()
        setupNavigation()
        setupAutoPrice()
        setupButton()
    }

    private fun initView() {

        spinnerType = findViewById(R.id.spinnerType)
        spinnerCity = findViewById(R.id.spinnerCity)

        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        edtWeight = findViewById(R.id.edtWeight)
        edtLength = findViewById(R.id.edtLength)
        edtWidth = findViewById(R.id.edtWidth)
        edtHeight = findViewById(R.id.edtHeight)
        edtPrice = findViewById(R.id.edtPrice)
    }

    private fun setupSpinner() {

        val packageType = arrayOf("Regular", "Express", "Same Day", "Cargo")

        spinnerType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            packageType
        )

        val cities = arrayOf("Jakarta", "Bandung", "Surabaya", "Medan", "Makassar")

        spinnerCity.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            cities
        )
    }

    private fun setupAutoPrice() {

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculatePrice()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        edtWeight.addTextChangedListener(watcher)
        edtLength.addTextChangedListener(watcher)
        edtWidth.addTextChangedListener(watcher)
        edtHeight.addTextChangedListener(watcher)
    }

    private fun calculatePrice() {

        val weight = edtWeight.text.toString().toDoubleOrNull() ?: 0.0
        val length = edtLength.text.toString().toDoubleOrNull() ?: 0.0
        val width = edtWidth.text.toString().toDoubleOrNull() ?: 0.0
        val height = edtHeight.text.toString().toDoubleOrNull() ?: 0.0

        val volume = (length * width * height) / 6000

        val baseRate = 10000

        val cityMultiplier = when (spinnerCity.selectedItem.toString()) {
            "Jakarta" -> 1.1
            "Bandung" -> 1.0
            "Surabaya" -> 1.3
            "Medan" -> 1.5
            "Makassar" -> 1.7
            else -> 1.0
        }

        val total = ((weight + volume) * baseRate * cityMultiplier).toInt()

        edtPrice.setText(total.toString())
    }

    private fun setupNavigation() {

        bottomNavigation.selectedItemId = R.id.menu_shipment

        bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.menu_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }

                R.id.menu_shipment -> true

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

    private fun setupButton() {

        btnSave.setOnClickListener {

            val data = ShipmentModel(
                receiver = findViewById<EditText>(R.id.edtReceiver).text.toString(),
                phone = findViewById<EditText>(R.id.edtPhone).text.toString(),
                address = findViewById<EditText>(R.id.edtAddress).text.toString(),
                city = spinnerCity.selectedItem.toString(),
                type = spinnerType.selectedItem.toString(),
                weight = edtWeight.text.toString().toDoubleOrNull() ?: 0.0,
                price = edtPrice.text.toString().toIntOrNull() ?: 0
            )

            ShipmentRepository.add(data)

            Toast.makeText(this, "Data tersimpan", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }

    }

}