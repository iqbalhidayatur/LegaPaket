package com.example.legapaket

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
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

    // Simpan data sebagai MutableList agar bisa diedit/hapus
    private val reportData: MutableList<ReportModel> = mutableListOf()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) doExport()
        else Toast.makeText(this, "Izin storage diperlukan untuk ekspor", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        rvReports      = findViewById(R.id.rvReports)
        tvTotal        = findViewById(R.id.tvTotalPackage)
        tvDelivered    = findViewById(R.id.tvDelivered)
        tvPending      = findViewById(R.id.tvPending)
        btnExportExcel = findViewById(R.id.btnExportExcel)

        rvReports.layoutManager = LinearLayoutManager(this)

        loadData()
        setupAdapter()
        updateSummary()

        btnExportExcel.setOnClickListener { exportToExcel() }
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        loadData()
        setupAdapter()
        updateSummary()
    }

    // ─── Load data dari repository ────────────────────────────────────────────

    private fun loadData() {
        reportData.clear()
        ShipmentRepository.getAll().mapIndexedTo(reportData) { index, it ->
            ReportModel(
                resi          = it.resi,
                receiver      = it.receiver,
                city          = it.city,
                status        = "Processing",
                date          = "Hari ini",
                type          = it.type,
                weight        = it.weight,
                price         = it.price,
                paymentMethod = it.paymentMethod
            )
        }
    }

    // ─── Setup adapter dengan callback edit & hapus ───────────────────────────

    private fun setupAdapter() {
        adapter = ReportAdapter(
            list = reportData,
            onEdit = { index, item -> showEditDialog(index, item) },
            onDelete = { index -> deleteItem(index) }
        )
        rvReports.adapter = adapter
    }

    // ─── Hapus item ───────────────────────────────────────────────────────────

    private fun deleteItem(index: Int) {
        // Hapus dari repository
        ShipmentRepository.delete(index)
        // Hapus dari list lokal
        reportData.removeAt(index)
        adapter.notifyItemRemoved(index)
        adapter.notifyItemRangeChanged(index, reportData.size)
        updateSummary()
        Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
    }

    // ─── Dialog Edit ──────────────────────────────────────────────────────────

    private fun showEditDialog(index: Int, item: ReportModel) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_edit_shipment, null)

        // Referensi view dalam dialog
        val edtReceiver  = dialogView.findViewById<EditText>(R.id.edtEditReceiver)
        val edtCity      = dialogView.findViewById<EditText>(R.id.edtEditCity)
        val edtType      = dialogView.findViewById<EditText>(R.id.edtEditType)
        val edtWeight    = dialogView.findViewById<EditText>(R.id.edtEditWeight)
        val edtPrice     = dialogView.findViewById<EditText>(R.id.edtEditPrice)
        val spinnerPayment = dialogView.findViewById<Spinner>(R.id.spinnerEditPayment)

        // Isi data saat ini
        edtReceiver.setText(item.receiver)
        edtCity.setText(item.city)
        edtType.setText(item.type)
        edtWeight.setText(item.weight.toString())
        edtPrice.setText(item.price.toString())

        // Setup spinner metode pembayaran
        val paymentMethods = listOf(
            PaymentMethod("GoPay",     R.drawable.ic_gopay),
            PaymentMethod("DANA",      R.drawable.ic_dana),
            PaymentMethod("ShopeePay", R.drawable.ic_shopee),
            PaymentMethod("QRIS",      R.drawable.ic_qris)
        )
        val paymentAdapter = PaymentMethodAdapter(this, paymentMethods)
        spinnerPayment.adapter = paymentAdapter

        // Set posisi spinner ke nilai saat ini
        val currentPaymentIndex = paymentMethods.indexOfFirst { it.name == item.paymentMethod }
        if (currentPaymentIndex >= 0) spinnerPayment.setSelection(currentPaymentIndex)

        var selectedPayment = item.paymentMethod
        spinnerPayment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: android.view.View?, pos: Int, id: Long) {
                selectedPayment = paymentMethods[pos].name
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Bangun dialog
        AlertDialog.Builder(this)
            .setTitle("Edit Data Pengiriman")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                // Validasi input
                val receiver = edtReceiver.text.toString().trim()
                val city     = edtCity.text.toString().trim()
                val type     = edtType.text.toString().trim()
                val weight   = edtWeight.text.toString().toDoubleOrNull()
                val price    = edtPrice.text.toString().toIntOrNull()

                if (receiver.isBlank() || city.isBlank() || type.isBlank()
                    || weight == null || weight <= 0 || price == null || price < 0) {
                    Toast.makeText(
                        this,
                        "Harap isi semua field dengan benar",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                // Update repository (ShipmentModel)
                val existing = ShipmentRepository.getAll()[index]
                val updated = existing.copy(
                    receiver      = receiver,
                    city          = city,
                    type          = type,
                    weight        = weight,
                    price         = price,
                    paymentMethod = selectedPayment
                )
                ShipmentRepository.update(index, updated)

                // Update list lokal & UI
                reportData[index] = item.copy(
                    receiver      = receiver,
                    city          = city,
                    type          = type,
                    weight        = weight,
                    price         = price,
                    paymentMethod = selectedPayment
                )
                adapter.notifyItemChanged(index)
                updateSummary()
                dialog.dismiss()
                Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // ─── Summary card ─────────────────────────────────────────────────────────

    private fun updateSummary() {
        tvTotal.text     = reportData.size.toString()
        tvDelivered.text = reportData.count { it.status == "Delivered" }.toString()
        tvPending.text   = reportData.count { it.status == "Processing" }.toString()
    }

    // ─── Export Excel (tidak berubah) ─────────────────────────────────────────

    private fun exportToExcel() {
        if (reportData.isEmpty()) {
            Toast.makeText(this, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            doExport()
        } else {
            val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                doExport()
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun doExport() {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName  = "LapLegaPaket_$timestamp.xlsx"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)
            writeXlsx(file)
            android.media.MediaScannerConnection.scanFile(
                this, arrayOf(file.absolutePath),
                arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), null
            )
            Toast.makeText(this, "Tersimpan di Downloads/$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun writeXlsx(file: File) {
        val headers = listOf("No","No. Resi","Penerima","Kota Tujuan","Tipe Pengiriman","Berat (Kg)","Ongkir (Rp)","Metode Pembayaran","Status","Tanggal")
        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheetData>""")
        sb.append("""<row r="1">""")
        headers.forEachIndexed { col, title ->
            sb.append("""<c r="${colLetter(col)}1" t="inlineStr" s="1"><is><t>${title.xmlEscape()}</t></is></c>""")
        }
        sb.append("</row>")
        reportData.forEachIndexed { idx, item ->
            val rowNum = idx + 2
            sb.append("""<row r="$rowNum">""")
            listOf((idx+1).toString(), item.resi, item.receiver, item.city, item.type, item.weight.toString(), item.price.toString(), item.paymentMethod, item.status, item.date)
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