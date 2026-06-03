package com.example.legapaket

import android.content.Intent
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ResiActivity : AppCompatActivity() {

    private lateinit var webViewResi: WebView
    private lateinit var btnExportPdf: MaterialButton
    private lateinit var btnSkip: MaterialButton

    // Data shipment diterima via Intent
    private var resiNumber = ""
    private var receiverName = ""
    private var receiverPhone = ""
    private var receiverAddress = ""
    private var receiverCity = ""
    private var packageType = ""
    private var weight = 0.0
    private var price = 0
    private var paymentMethod = ""

    private var senderName = ""
    private var senderPhone = ""
    private var senderAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resi)

        // Ambil data dari Intent
        resiNumber    = intent.getStringExtra("resi_number") ?: "RESI-000"
        receiverName  = intent.getStringExtra("receiver_name") ?: "-"
        receiverPhone = intent.getStringExtra("receiver_phone") ?: "-"
        receiverAddress = intent.getStringExtra("receiver_address") ?: "-"
        receiverCity  = intent.getStringExtra("receiver_city") ?: "-"
        packageType   = intent.getStringExtra("package_type") ?: "-"
        weight        = intent.getDoubleExtra("weight", 0.0)
        price         = intent.getIntExtra("price", 0)
        paymentMethod = intent.getStringExtra("payment_method") ?: "-"

        senderName    = intent.getStringExtra("sender_name") ?: "-"
        senderPhone   = intent.getStringExtra("sender_phone") ?: "-"
        senderAddress = intent.getStringExtra("sender_address") ?: "-"

        webViewResi   = findViewById(R.id.webViewResi)
        btnExportPdf  = findViewById(R.id.btnExportPdf)
        btnSkip       = findViewById(R.id.btnSkip)

        setupWebView()

        btnExportPdf.setOnClickListener { exportToPdf() }

        btnSkip.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    private fun setupWebView() {
        webViewResi.settings.javaScriptEnabled = true
        webViewResi.settings.builtInZoomControls = false
        webViewResi.settings.setSupportZoom(false)

        val formattedPrice = String.format("%,d", price).replace(",", ".")

        val htmlContent = buildHtml(
            resi          = resiNumber,
            senderName    = senderName,      // ← TAMBAH
            senderPhone   = senderPhone,     // ← TAMBAH
            senderAddress = senderAddress,   // ← TAMBAH
            name          = receiverName,
            phone         = receiverPhone,
            address       = receiverAddress,
            city          = receiverCity,
            type          = packageType,
            weightStr     = "$weight Kg",
            payment       = paymentMethod,
            totalPrice    = "Rp $formattedPrice"
        )

        webViewResi.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView, request: WebResourceRequest
            ) = true
        }

        // ← Ganti dari file:///android_asset/ — base URL untuk folder assets
        webViewResi.loadDataWithBaseURL(
            "file:///android_asset/",
            htmlContent,
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun exportToPdf() {
        val printManager = getSystemService(PRINT_SERVICE) as PrintManager
        val jobName = "Resi_$resiNumber"

        val printAdapter = webViewResi.createPrintDocumentAdapter(jobName)

        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        printManager.print(jobName, printAdapter, printAttributes)

        Toast.makeText(this, "Membuka dialog cetak / simpan PDF...", Toast.LENGTH_SHORT).show()
    }

    // Template HTML resi — inject data nyata dari ShipmentModel
    private fun buildHtml(
        resi: String,
        senderName: String, senderPhone: String, senderAddress: String,  // ← TAMBAH
        name: String, phone: String, address: String, city: String,
        type: String, weightStr: String, payment: String, totalPrice: String
    ): String = """
    <!DOCTYPE html>
    <html lang="id">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Resi Pengiriman</title>
        <style>
            body { font-family: Arial, sans-serif; background: #f4f4f4; padding: 20px; }
            .receipt { max-width: 700px; margin: auto; background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
            .header { border-bottom: 2px dashed #ccc; padding-bottom: 16px; margin-bottom: 20px; text-align: center; }
            .header h1 { margin: 0; font-size: 2rem; }
            .header p { font-size: 1.2rem; margin-top: 6px; color: #666; }
            .resi-number { background: #DC127A; color: #fff; display: inline-block; padding: 8px 14px; border-radius: 8px; font-size: 1.2rem; }
            .section { margin-bottom: 20px; }
            .section-title { font-size: 1.2rem; font-weight: bold; margin-bottom: 10px; border-left: 4px solid #000; padding-left: 10px; }
            .row { display: flex; justify-content: space-between; margin-bottom: 10px; gap: 20px; }
            .item { flex: 1; }
            .label { font-size: 1.2rem; color: #666; margin-bottom: 4px; }
            .value { font-size: 1.2rem; font-weight: bold; }
            .price-box { background: #f7f7f7; border-radius: 10px; padding: 16px; }
            .footer { text-align: center; margin-top: 30px; color: #777; font-size: 1.2rem; }
            .divider { border: none; border-top: 1px dashed #ccc; margin: 16px 0; }
        </style>
    </head>
    <body>
    <div class="receipt">
        <div class="header">
            <h1>LEGA PAKET</h1>
            <p>Resi Pengiriman Barang</p>
            <div class="resi-number">$resi</div>
        </div>

        <!-- Data Pengirim -->
        <div class="section">
            <div class="section-title">Data Pengirim</div>
            <div class="row">
                <div class="item"><div class="label">Nama Pengirim</div><div class="value">$senderName</div></div>
                <div class="item"><div class="label">No. Telepon</div><div class="value">$senderPhone</div></div>
            </div>
            <div class="row">
                <div class="item"><div class="label">Alamat Pengirim</div><div class="value">$senderAddress</div></div>
            </div>
        </div>

        <hr class="divider"/>

        <!-- Data Penerima -->
        <div class="section">
            <div class="section-title">Data Penerima</div>
            <div class="row">
                <div class="item"><div class="label">Nama Penerima</div><div class="value">$name</div></div>
                <div class="item"><div class="label">No. Telepon</div><div class="value">$phone</div></div>
            </div>
            <div class="row">
                <div class="item"><div class="label">Alamat</div><div class="value">$address</div></div>
            </div>
            <div class="row">
                <div class="item"><div class="label">Kota Tujuan</div><div class="value">$city</div></div>
            </div>
        </div>

        <!-- Detail Pengiriman -->
        <div class="section">
            <div class="section-title">Detail Pengiriman</div>
            <div class="row">
                <div class="item"><div class="label">Jenis Pengiriman</div><div class="value">$type</div></div>
                <div class="item"><div class="label">Berat Barang</div><div class="value">$weightStr</div></div>
            </div>
            <div class="row">
                <div class="item"><div class="label">Metode Pembayaran</div><div class="value">$payment</div></div>
            </div>
        </div>

        <!-- Total -->
        <div class="section">
            <div class="section-title">Total Pembayaran</div>
            <div class="price-box">
                <div class="label">Total Harga</div>
                <div class="value">$totalPrice</div>
            </div>
        </div>

        <div class="footer">Terima kasih telah menggunakan layanan Lega Paket</div>
    </div>
    </body>
    </html>
""".trimIndent()
}