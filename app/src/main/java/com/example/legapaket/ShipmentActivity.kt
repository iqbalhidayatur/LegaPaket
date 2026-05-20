package com.example.legapaket

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class ShipmentActivity : AppCompatActivity() {

    private lateinit var spinnerType: Spinner
    private lateinit var spinnerCity: Spinner
    private lateinit var spinnerPayment: Spinner  // ← TAMBAH

    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var edtWeight: EditText
    private lateinit var edtLength: EditText
    private lateinit var edtWidth: EditText
    private lateinit var edtHeight: EditText
    private lateinit var edtPrice: EditText

    // ← TAMBAH: disimpan di level class agar bisa diakses setupButton()
    private var selectedPayment = "GoPay"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipment)

        initView()
        setupSpinner()
        setupPaymentSpinner()  // ← TAMBAH
        setupNavigation()
        setupAutoPrice()
        setupButton()
    }

    private fun initView() {
        spinnerType = findViewById(R.id.spinnerType)
        spinnerCity = findViewById(R.id.spinnerCity)
        spinnerPayment = findViewById(R.id.spinner_payment)  // ← TAMBAH

        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        edtWeight = findViewById(R.id.edtWeight)
        edtLength = findViewById(R.id.edtLength)
        edtWidth = findViewById(R.id.edtWidth)
        edtHeight = findViewById(R.id.edtHeight)
        edtPrice = findViewById(R.id.edtPrice)
    }

    // ← TAMBAH: fungsi khusus setup spinner pembayaran
    private fun setupPaymentSpinner() {
        val paymentMethods = listOf(
            PaymentMethod("GoPay",     R.drawable.ic_gopay),
            PaymentMethod("DANA",      R.drawable.ic_dana),
            PaymentMethod("ShopeePay", R.drawable.ic_shopee),
            PaymentMethod("QRIS",      R.drawable.ic_qris)
        )

        val paymentAdapter = PaymentMethodAdapter(this, paymentMethods)
        spinnerPayment.adapter = paymentAdapter

        spinnerPayment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, pos: Int, id: Long
            ) {
                selectedPayment = paymentMethods[pos].name
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupSpinner() {
        val packageType = arrayOf("Regular", "Express", "Same Day", "Cargo")
        spinnerType.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, packageType
        )

        val cities = arrayOf("Jakarta", "Bandung", "Surabaya", "Medan", "Makassar")
        spinnerCity.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, cities
        )
    }

    private fun setupAutoPrice() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { calculatePrice() }
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
        val width  = edtWidth.text.toString().toDoubleOrNull()  ?: 0.0
        val height = edtHeight.text.toString().toDoubleOrNull() ?: 0.0

        val volume = (length * width * height) / 6000
        val baseRate = 10000

        val cityMultiplier = when (spinnerCity.selectedItem.toString()) {
            "Jakarta"  -> 1.1
            "Bandung"  -> 1.0
            "Surabaya" -> 1.3
            "Medan"    -> 1.5
            "Makassar" -> 1.7
            else       -> 1.0
        }

        val total = ((weight + volume) * baseRate * cityMultiplier).toInt()
        edtPrice.setText(total.toString())
    }

    private fun setupNavigation() {
        bottomNavigation.selectedItemId = R.id.menu_shipment
        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java)); true
                }
                R.id.menu_shipment -> true
                R.id.menu_reports -> {
                    startActivity(Intent(this, ReportsActivity::class.java)); true
                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java)); true
                }
                else -> false
            }
        }
    }

    private fun setupButton() {
        btnSave.setOnClickListener {

            // Validasi sederhana
            val receiver = findViewById<EditText>(R.id.edtReceiver).text.toString()
            val phone    = findViewById<EditText>(R.id.edtPhone).text.toString()
            val address  = findViewById<EditText>(R.id.edtAddress).text.toString()

            if (receiver.isBlank() || phone.isBlank() || address.isBlank()) {
                Toast.makeText(this, "Lengkapi semua data terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = ShipmentModel(
                receiver      = receiver,
                phone         = phone,
                address       = address,
                city          = spinnerCity.selectedItem.toString(),
                type          = spinnerType.selectedItem.toString(),
                weight        = edtWeight.text.toString().toDoubleOrNull() ?: 0.0,
                price         = edtPrice.text.toString().toIntOrNull() ?: 0,
                paymentMethod = selectedPayment
            )

            ShipmentRepository.add(data)

            // Generate nomor resi unik
            val resiNumber = "LP-${System.currentTimeMillis()}"

            // Navigasi ke ResiActivity dengan membawa semua data
            val intent = Intent(this, ResiActivity::class.java).apply {
                putExtra("resi_number",      resiNumber)
                putExtra("receiver_name",    data.receiver)
                putExtra("receiver_phone",   data.phone)
                putExtra("receiver_address", data.address)
                putExtra("receiver_city",    data.city)
                putExtra("package_type",     data.type)
                putExtra("weight",           data.weight)
                putExtra("price",            data.price)
                putExtra("payment_method",   data.paymentMethod)
            }
            startActivity(intent)
            finish()
        }

        btnCancel.setOnClickListener { finish() }
    }
}