LegaPaket Mobile App

Aplikasi mobile operasional ekspedisi berbasis Android menggunakan Kotlin dan XML View System.

LegaPaket membantu agen ekspedisi melakukan input pengiriman, monitoring aktivitas paket, dan melihat laporan operasional langsung dari perangkat mobile.

Fitur Utama
Dashboard operasional
Input data shipment
Perhitungan ongkir otomatis
Reports pengiriman
Bottom navigation
RecyclerView activity tracking
Auto refresh dashboard
Statistik paket
Material Design 3 UI
Responsive layout
Teknologi
Kotlin
XML View System
Android Studio
Material Design 3
RecyclerView
ConstraintLayout
BottomNavigationView
Struktur Halaman
Dashboard

Menampilkan:

Total paket masuk
Paket terkirim
Paket pending
Aktivitas pengiriman terbaru
Shipment

Fitur:

Input data penerima
Input alamat
Dropdown kota tujuan
Dropdown tipe paket
Perhitungan ongkir otomatis berdasarkan:
tujuan
berat
dimensi paket
Reports

Menampilkan:

Total shipment
Status pengiriman
Riwayat laporan pengiriman
Profile

Menampilkan:

Informasi akun agen
Logout


Struktur Project

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



Cara Menjalankan
Clone repository
git clone https://github.com/iqbalhidayaturr/legapaket.git
Buka project di Android Studio
Sync Gradle
Jalankan emulator atau device Android
Klik Run
Perhitungan Ongkir

Rumus dasar:

(volume + berat) x tarif dasar x multiplier kota

Volume:

panjang x lebar x tinggi / 6000
Status Project

Development

Pengembangan Selanjutnya
Room Database
Firebase Authentication
API Tracking
Push Notification
Export PDF Reports
Grafik Statistik
Dark Mode
Multi Role User
Scan Barcode AWB


Documentation :

UML Diagram

https://drive.google.com/drive/u/4/folders/1okRUgtoXEFw93Vwpiyh5cBSydzPt428q

UI Design

https://www.figma.com/design/JSzP9h97d7DK3ZBWtiEUuj/Legapaket-UI-Final?node-id=0-1&p=f&t=3MCYI5M5qtbNZhh6-0


Author

Iqbal Hidayatur Rahman

Laila Ramadanisa

Haikal Zhifa Wisesa

Gilang M Rizki
