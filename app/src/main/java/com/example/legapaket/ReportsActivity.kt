package com.example.legapaket

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
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

    private var reportData: List<ReportModel> = emptyList()

    // Permission launcher untuk Android < 10
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        android.util.Log.d("EXCEL_DEBUG", "Permission result: $isGranted")
        if (isGranted) {
            doExport()
        } else {
            Toast.makeText(this, "Izin storage diperlukan untuk ekspor", Toast.LENGTH_LONG).show()
        }
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
        reportData = loadData()
        adapter = ReportAdapter(reportData)
        rvReports.adapter = adapter
        updateSummary(reportData)

        android.util.Log.d("EXCEL_DEBUG", "onCreate selesai, data: ${reportData.size} item")
        android.util.Log.d("EXCEL_DEBUG", "Android SDK: ${Build.VERSION.SDK_INT}")

        btnExportExcel.setOnClickListener {
            android.util.Log.d("EXCEL_DEBUG", "TOMBOL DIKLIK")
            exportToExcel()
        }

        setupNavigation()
    }

    private fun exportToExcel() {
        android.util.Log.d("EXCEL_DEBUG", "exportToExcel() dipanggil")

        if (reportData.isEmpty()) {
            android.util.Log.d("EXCEL_DEBUG", "Data kosong!")
            Toast.makeText(this, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show()
            return
        }

        // Android 10+ tidak butuh permission untuk Downloads
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            android.util.Log.d("EXCEL_DEBUG", "Android 10+, langsung ekspor")
            doExport()
        } else {
            // Android 9 ke bawah butuh permission WRITE_EXTERNAL_STORAGE
            val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                android.util.Log.d("EXCEL_DEBUG", "Permission sudah ada, langsung ekspor")
                doExport()
            } else {
                android.util.Log.d("EXCEL_DEBUG", "Minta permission...")
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun doExport() {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName  = "LapLegaPaket_$timestamp.xlsx"

            val downloadsDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )

            android.util.Log.d("EXCEL_DEBUG", "downloadsDir: ${downloadsDir.absolutePath}")
            android.util.Log.d("EXCEL_DEBUG", "downloadsDir exists: ${downloadsDir.exists()}")
            android.util.Log.d("EXCEL_DEBUG", "downloadsDir canWrite: ${downloadsDir.canWrite()}")

            val mkdirResult = downloadsDir.mkdirs()
            android.util.Log.d("EXCEL_DEBUG", "mkdirs result: $mkdirResult")

            val file = File(downloadsDir, fileName)
            android.util.Log.d("EXCEL_DEBUG", "File path: ${file.absolutePath}")

            writeXlsx(file)

            android.util.Log.d("EXCEL_DEBUG", "writeXlsx selesai")
            android.util.Log.d("EXCEL_DEBUG", "File exists: ${file.exists()}, size: ${file.length()} bytes")

            // Notify media scanner agar file muncul di file manager
            android.media.MediaScannerConnection.scanFile(
                this,
                arrayOf(file.absolutePath),
                arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                null
            )

            Toast.makeText(this, "✅ Tersimpan di Downloads/$fileName", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            android.util.Log.e("EXCEL_DEBUG", "EXCEPTION: ${e.javaClass.simpleName}: ${e.message}", e)
            Toast.makeText(this, "Gagal: ${e.javaClass.simpleName}\n${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun writeXlsx(file: File) {
        android.util.Log.d("EXCEL_DEBUG", "writeXlsx() mulai")

        val headers = listOf(
            "No", "No. Resi", "Penerima", "Kota Tujuan",
            "Tipe Pengiriman", "Berat (Kg)", "Ongkir (Rp)",
            "Metode Pembayaran", "Status", "Tanggal"
        )

        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">""")
        sb.append("<sheetData>")

        sb.append("""<row r="1">""")
        headers.forEachIndexed { col, title ->
            val cellRef = colLetter(col) + "1"
            sb.append("""<c r="$cellRef" t="inlineStr" s="1"><is><t>${title.xmlEscape()}</t></is></c>""")
        }
        sb.append("</row>")

        reportData.forEachIndexed { idx, item ->
            val rowNum = idx + 2
            sb.append("""<row r="$rowNum">""")
            listOf(
                (idx + 1).toString(),
                item.resi,
                item.receiver,
                item.city,
                item.type,
                item.weight.toString(),
                item.price.toString(),
                item.paymentMethod,
                item.status,
                item.date
            ).forEachIndexed { col, value ->
                val cellRef = colLetter(col) + rowNum
                sb.append("""<c r="$cellRef" t="inlineStr"><is><t>${value.xmlEscape()}</t></is></c>""")
            }
            sb.append("</row>")
        }
        sb.append("</sheetData></worksheet>")

        val relsXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>"""

        val workbookXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
          xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets><sheet name="Laporan Pengiriman" sheetId="1" r:id="rId1"/></sheets>
</workbook>"""

        val workbookRelsXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""

        val stylesXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts>
    <font><sz val="11"/><name val="Arial"/></font>
    <font><b/><sz val="11"/><name val="Arial"/></font>
  </fonts>
  <fills>
    <fill><patternFill patternType="none"/></fill>
    <fill><patternFill patternType="gray125"/></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FF4472C4"/></fgColor></patternFill>
  </fills>
  <borders><border><left/><right/><top/><bottom/><diagonal/></border></borders>
  <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
  <cellXfs>
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
    <xf numFmtId="0" fontId="1" fillId="2" borderId="0" xfId="0"><alignment horizontal="center"/></xf>
  </cellXfs>
</styleSheet>"""

        val contentTypesXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
</Types>"""

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

        android.util.Log.d("EXCEL_DEBUG", "writeXlsx() selesai")
    }

    private fun colLetter(index: Int): String {
        var n = index
        var result = ""
        do {
            result = ('A' + (n % 26)).toString() + result
            n = n / 26 - 1
        } while (n >= 0)
        return result
    }

    private fun String.xmlEscape(): String = this
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")

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

    private fun updateSummary(list: List<ReportModel>) {
        tvTotal.text     = list.size.toString()
        tvDelivered.text = list.count { it.status == "Delivered" }.toString()
        tvPending.text   = list.count { it.status == "Processing" }.toString()
    }

    private fun loadData(): List<ReportModel> {
        return ShipmentRepository.getAll().mapIndexed { index, it ->
            ReportModel(
                resi          = "LP-${System.currentTimeMillis() + index}",
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
}