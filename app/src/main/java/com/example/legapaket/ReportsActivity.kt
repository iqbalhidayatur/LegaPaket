package com.example.legapaket

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ReportsActivity : AppCompatActivity() {

    private lateinit var rvReports: RecyclerView
    private lateinit var adapter: ReportAdapter
    private lateinit var tvTotal: TextView
    private lateinit var tvDelivered: TextView
    private lateinit var tvPending: TextView
    private lateinit var btnExportExcel: MaterialButton

    // ─── Filter & Sort views ───────────────────────────────────────────────────
    private lateinit var spinnerCity: Spinner
    private lateinit var spinnerSort: Spinner
    private lateinit var tvDateStart: TextView
    private lateinit var tvDateEnd: TextView
    private lateinit var btnResetFilter: MaterialButton

    // Format tanggal & jam untuk tampilan item list
    private val displayFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    // Format tanggal untuk label tombol rentang tanggal
    private val dateLabel     = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    // Semua data mentah dari repository (tidak pernah difilter langsung)
    private val allData: MutableList<ReportModel> = mutableListOf()
    // Data yang ditampilkan (hasil filter + sort)
    private val displayData: MutableList<ReportModel> = mutableListOf()

    // State filter aktif
    private var filterCity   = "Semua Kota"
    private var filterStart: Calendar? = null
    private var filterEnd: Calendar?   = null
    private var sortMode     = "Terbaru"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) doExport()
        else Toast.makeText(this, "Izin storage diperlukan untuk ekspor", Toast.LENGTH_LONG).show()
    }

    // ─────────────────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        bindViews()
        setupFilterBar()
        loadData()
        applyFilterAndSort()
        setupAdapter()
        updateSummary()

        btnExportExcel.setOnClickListener { exportToExcel() }
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        loadData()
        applyFilterAndSort()
        adapter.notifyDataSetChanged()
        updateSummary()
    }

    // ─── Bind views ──────────────────────────────────────────────────────────
    private fun bindViews() {
        rvReports      = findViewById(R.id.rvReports)
        tvTotal        = findViewById(R.id.tvTotalPackage)
        tvDelivered    = findViewById(R.id.tvDelivered)
        tvPending      = findViewById(R.id.tvPending)
        btnExportExcel = findViewById(R.id.btnExportExcel)

        spinnerCity    = findViewById(R.id.spinnerFilterCity)
        spinnerSort    = findViewById(R.id.spinnerSort)
        tvDateStart    = findViewById(R.id.tvDateStart)
        tvDateEnd      = findViewById(R.id.tvDateEnd)
        btnResetFilter = findViewById(R.id.btnResetFilter)

        rvReports.layoutManager = LinearLayoutManager(this)
    }

    // ─── Setup dropdown filter & sort ────────────────────────────────────────
    private fun setupFilterBar() {

        // Spinner kota (diambil dinamis dari data + "Semua Kota")
        val cities = listOf("Semua Kota", "Jakarta", "Bandung", "Surabaya", "Medan", "Makassar")
        spinnerCity.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cities)
        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                filterCity = cities[pos]
                applyFilterAndSort()
                adapter.notifyDataSetChanged()
                updateSummary()
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        // Spinner urutan
        val sortOptions = listOf("Terbaru", "Terlama", "Ongkir Tertinggi", "Ongkir Terendah")
        spinnerSort.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortOptions)
        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                sortMode = sortOptions[pos]
                applyFilterAndSort()
                adapter.notifyDataSetChanged()
                updateSummary()
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        // Tombol pilih tanggal mulai
        tvDateStart.setOnClickListener { showDatePicker(isStart = true) }

        // Tombol pilih tanggal selesai
        tvDateEnd.setOnClickListener { showDatePicker(isStart = false) }

        // Reset semua filter
        btnResetFilter.setOnClickListener {
            filterCity  = "Semua Kota"
            filterStart = null
            filterEnd   = null
            sortMode    = "Terbaru"
            spinnerCity.setSelection(0)
            spinnerSort.setSelection(0)
            tvDateStart.text = "Tanggal Mulai"
            tvDateEnd.text   = "Tanggal Selesai"
            applyFilterAndSort()
            adapter.notifyDataSetChanged()
            updateSummary()
        }
    }

    // ─── DatePickerDialog ─────────────────────────────────────────────────────
    private fun showDatePicker(isStart: Boolean) {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            val picked = Calendar.getInstance().apply {
                set(year, month, day)
                if (isStart) { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }
                else         { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59) }
            }
            if (isStart) {
                filterStart = picked
                tvDateStart.text = dateLabel.format(picked.time)
            } else {
                // Validasi: tanggal selesai tidak boleh sebelum tanggal mulai
                if (filterStart != null && picked.before(filterStart)) {
                    Toast.makeText(this, "Tanggal selesai tidak boleh sebelum tanggal mulai", Toast.LENGTH_SHORT).show()
                    return@DatePickerDialog
                }
                filterEnd = picked
                tvDateEnd.text = dateLabel.format(picked.time)
            }
            applyFilterAndSort()
            adapter.notifyDataSetChanged()
            updateSummary()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    // ─── Load data dari repository ────────────────────────────────────────────
    private fun loadData() {
        allData.clear()
        ShipmentRepository.getAll().forEach { it ->
            allData.add(
                ReportModel(
                    resi          = it.resi,
                    receiver      = it.receiver,
                    city          = it.city,
                    status        = "Processing",
                    date          = displayFormat.format(it.createdAt),
                    createdAt     = it.createdAt,
                    type          = it.type,
                    weight        = it.weight,
                    price         = it.price,
                    paymentMethod = it.paymentMethod
                )
            )
        }
    }

    // ─── Filter + Sort → isi displayData ─────────────────────────────────────
    private fun applyFilterAndSort() {
        var filtered = allData.toList()

        // Filter kota
        if (filterCity != "Semua Kota") {
            filtered = filtered.filter { it.city == filterCity }
        }

        // Filter rentang tanggal
        filterStart?.let { start -> filtered = filtered.filter { !it.createdAt.before(start.time) } }
        filterEnd?.let   { end   -> filtered = filtered.filter { !it.createdAt.after(end.time) } }

        // Urutan
        filtered = when (sortMode) {
            "Terbaru"          -> filtered.sortedByDescending { it.createdAt }
            "Terlama"          -> filtered.sortedBy { it.createdAt }
            "Ongkir Tertinggi" -> filtered.sortedByDescending { it.price }
            "Ongkir Terendah"  -> filtered.sortedBy { it.price }
            else               -> filtered
        }

        displayData.clear()
        displayData.addAll(filtered)
    }

    // ─── Setup adapter ────────────────────────────────────────────────────────
    private fun setupAdapter() {
        adapter = ReportAdapter(
            list     = displayData,
            onEdit   = { index, item -> showEditDialog(index, item) },
            onDelete = { index -> deleteItem(index) }
        )
        rvReports.adapter = adapter
    }

    // ─── Hapus item ───────────────────────────────────────────────────────────
    private fun deleteItem(displayIndex: Int) {
        val item = displayData[displayIndex]
        // Cari index asli di allData & repository berdasarkan resi (unik)
        val repoIndex = ShipmentRepository.getAll().indexOfFirst { it.resi == item.resi }
        if (repoIndex != -1) ShipmentRepository.delete(repoIndex)

        allData.removeAll { it.resi == item.resi }
        displayData.removeAt(displayIndex)
        adapter.notifyItemRemoved(displayIndex)
        adapter.notifyItemRangeChanged(displayIndex, displayData.size)
        updateSummary()
        Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
    }

    // ─── Dialog Edit ──────────────────────────────────────────────────────────
    private fun showEditDialog(displayIndex: Int, item: ReportModel) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_shipment, null)

        val edtReceiver    = dialogView.findViewById<EditText>(R.id.edtEditReceiver)
        val edtCity        = dialogView.findViewById<EditText>(R.id.edtEditCity)
        val edtType        = dialogView.findViewById<EditText>(R.id.edtEditType)
        val edtWeight      = dialogView.findViewById<EditText>(R.id.edtEditWeight)
        val edtPrice       = dialogView.findViewById<EditText>(R.id.edtEditPrice)
        val spinnerPayment = dialogView.findViewById<Spinner>(R.id.spinnerEditPayment)

        edtReceiver.setText(item.receiver)
        edtCity.setText(item.city)
        edtType.setText(item.type)
        edtWeight.setText(item.weight.toString())
        edtPrice.setText(item.price.toString())

        val paymentMethods = listOf(
            PaymentMethod("GoPay",     R.drawable.ic_gopay),
            PaymentMethod("DANA",      R.drawable.ic_dana),
            PaymentMethod("ShopeePay", R.drawable.ic_shopee),
            PaymentMethod("QRIS",      R.drawable.ic_qris)
        )
        spinnerPayment.adapter = PaymentMethodAdapter(this, paymentMethods)

        val currentIdx = paymentMethods.indexOfFirst { it.name == item.paymentMethod }
        if (currentIdx >= 0) spinnerPayment.setSelection(currentIdx)

        var selectedPayment = item.paymentMethod
        spinnerPayment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) { selectedPayment = paymentMethods[pos].name }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Data Pengiriman")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val receiver = edtReceiver.text.toString().trim()
                val city     = edtCity.text.toString().trim()
                val type     = edtType.text.toString().trim()
                val weight   = edtWeight.text.toString().toDoubleOrNull()
                val price    = edtPrice.text.toString().toIntOrNull()

                if (receiver.isBlank() || city.isBlank() || type.isBlank()
                    || weight == null || weight <= 0 || price == null || price < 0) {
                    Toast.makeText(this, "Harap isi semua field dengan benar", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Update repository
                val repoIndex = ShipmentRepository.getAll().indexOfFirst { it.resi == item.resi }
                if (repoIndex != -1) {
                    val existing = ShipmentRepository.getAll()[repoIndex]
                    ShipmentRepository.update(repoIndex, existing.copy(
                        receiver = receiver, city = city, type = type,
                        weight = weight, price = price, paymentMethod = selectedPayment
                    ))
                }

                // Update allData & displayData
                val updatedItem = item.copy(
                    receiver = receiver, city = city, type = type,
                    weight = weight, price = price, paymentMethod = selectedPayment
                )
                val allIdx = allData.indexOfFirst { it.resi == item.resi }
                if (allIdx != -1) allData[allIdx] = updatedItem
                displayData[displayIndex] = updatedItem

                adapter.notifyItemChanged(displayIndex)
                updateSummary()
                dialog.dismiss()
                Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // ─── Summary card ─────────────────────────────────────────────────────────
    private fun updateSummary() {
        tvTotal.text     = displayData.size.toString()
        tvDelivered.text = displayData.count { it.status == "Delivered" }.toString()
        tvPending.text   = displayData.count { it.status == "Processing" }.toString()
    }

    // ─── Export Excel ─────────────────────────────────────────────────────────
    private fun exportToExcel() {
        if (displayData.isEmpty()) {
            Toast.makeText(this, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) doExport()
        else {
            val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) doExport()
            else requestPermissionLauncher.launch(perm)
        }
    }

    private fun doExport() {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName  = "LapLegaPaket_$timestamp.xlsx"
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            dir.mkdirs()
            val file = File(dir, fileName)
            writeXlsx(file)
            android.media.MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath),
                arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), null)
            Toast.makeText(this, "Tersimpan di Downloads/$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun writeXlsx(file: File) {
        val headers = listOf("No","No. Resi","Penerima","Kota Tujuan","Tipe Pengiriman","Berat (Kg)","Ongkir (Rp)","Metode Pembayaran","Status","Tanggal & Jam")
        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheetData>""")
        sb.append("""<row r="1">""")
        headers.forEachIndexed { col, title ->
            sb.append("""<c r="${colLetter(col)}1" t="inlineStr" s="1"><is><t>${title.xmlEscape()}</t></is></c>""")
        }
        sb.append("</row>")
        displayData.forEachIndexed { idx, item ->
            val rowNum = idx + 2
            sb.append("""<row r="$rowNum">""")
            listOf((idx+1).toString(), item.resi, item.receiver, item.city, item.type,
                item.weight.toString(), item.price.toString(), item.paymentMethod, item.status, item.date)
                .forEachIndexed { col, value ->
                    sb.append("""<c r="${colLetter(col)}$rowNum" t="inlineStr"><is><t>${value.xmlEscape()}</t></is></c>""")
                }
            sb.append("</row>")
        }
        sb.append("</sheetData></worksheet>")

        val relsXml         = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/></Relationships>"""
        val workbookXml     = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"><sheets><sheet name="Laporan Pengiriman" sheetId="1" r:id="rId1"/></sheets></workbook>"""
        val workbookRelsXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/><Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/></Relationships>"""
        val stylesXml       = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><fonts><font><sz val="11"/><name val="Arial"/></font><font><b/><sz val="11"/><name val="Arial"/></font></fonts><fills><fill><patternFill patternType="none"/></fill><fill><patternFill patternType="gray125"/></fill><fill><patternFill patternType="solid"><fgColor rgb="FF4472C4"/></fgColor></patternFill></fills><borders><border><left/><right/><top/><bottom/><diagonal/></border></borders><cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs><cellXfs><xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/><xf numFmtId="0" fontId="1" fillId="2" borderId="0" xfId="0"><alignment horizontal="center"/></xf></cellXfs></styleSheet>"""
        val contentTypesXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/><Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/><Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/></Types>"""

        java.util.zip.ZipOutputStream(java.io.BufferedOutputStream(file.outputStream())).use { zip ->
            fun addEntry(name: String, content: String) {
                zip.putNextEntry(java.util.zip.ZipEntry(name))
                zip.write(content.toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
            addEntry("[Content_Types].xml",        contentTypesXml)
            addEntry("_rels/.rels",                relsXml)
            addEntry("xl/workbook.xml",            workbookXml)
            addEntry("xl/_rels/workbook.xml.rels", workbookRelsXml)
            addEntry("xl/worksheets/sheet1.xml",   sb.toString())
            addEntry("xl/styles.xml",              stylesXml)
        }
    }

    private fun colLetter(index: Int): String {
        var n = index; var result = ""
        do { result = ('A' + (n % 26)).toString() + result; n = n / 26 - 1 } while (n >= 0)
        return result
    }

    private fun String.xmlEscape() = replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&apos;")

    // ─── Bottom navigation ────────────────────────────────────────────────────
    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.menu_reports
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_dashboard -> { startActivity(Intent(this, DashboardActivity::class.java)); true }
                R.id.menu_shipment  -> { startActivity(Intent(this, ShipmentActivity::class.java)); true }
                R.id.menu_reports   -> true
                R.id.menu_profile   -> { startActivity(Intent(this, ProfileActivity::class.java)); true }
                else -> false
            }
        }
    }
}