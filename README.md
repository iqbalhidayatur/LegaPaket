Aplikasi mobile operasional ekspedisi berbasis Android menggunakan Kotlin dan XML View System.

LegaPaket membantu agen ekspedisi melakukan input pengiriman, monitoring aktivitas paket, dan melihat laporan operasional langsung dari perangkat mobile.

## Fitur Utama

* Dashboard operasional
* Input data shipment
* Perhitungan ongkir otomatis
* Reports pengiriman
* Bottom navigation
* RecyclerView activity tracking
* Auto refresh dashboard
* Statistik paket
* Material Design 3 UI
* Responsive layout

## Teknologi

* Kotlin
* XML View System
* Android Studio
* Material Design 3
* RecyclerView
* ConstraintLayout
* BottomNavigationView

## Struktur Halaman

### Dashboard

Menampilkan:

* Total paket masuk
* Paket terkirim
* Paket pending
* Aktivitas pengiriman terbaru

### Shipment

Fitur:

* Input data penerima
* Input alamat
* Dropdown kota tujuan
* Dropdown tipe paket
* Perhitungan ongkir otomatis berdasarkan:

  * tujuan
  * berat
  * dimensi paket

### Reports

Menampilkan:

* Total shipment
* Status pengiriman
* Riwayat laporan pengiriman

### Profile

Menampilkan:

* Informasi akun agen
* Logout

---

## Struktur Project

```plaintext
com.example.legapaket
│
├── DashboardActivity.kt
├── ShipmentActivity.kt
├── ReportsActivity.kt
├── ProfileActivity.kt
│
├── ActivityAdapter.kt
├── ReportAdapter.kt
│
├── ActivityModel.kt
├── ShipmentModel.kt
├── ReportModel.kt
│
├── ShipmentRepository.kt
│
├── res
│   ├── layout
│   ├── drawable
│   ├── menu
│   ├── values
│   └── font
```

## Cara Menjalankan

1. Clone repository

```bash
git clone https://github.com/iqbalhidayatur/legapaket.git
```

2. Buka project di Android Studio

3. Sync Gradle

4. Jalankan emulator atau device Android

5. Klik Run

## Perhitungan Ongkir

Rumus dasar:

```plaintext
(volume + berat) x tarif dasar x multiplier kota
```

Volume:

```plaintext
panjang x lebar x tinggi / 6000
```

## Status Project

Development

## Author

  "Kelompok 5 TIF RP 24 A CID"
Iqbal Hidayatur Rahman 24552011201
Laila Ramadanisa       24552011214
Haikal Zhifa Wisesa    24552011239
Gilang M Rizki         22552011212

