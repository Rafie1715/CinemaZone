# CinemaZone 🎬

![Java](https://img.shields.io/badge/Made%20with-Kotlin-blue?logo=kotlin)
![Platform](https://img.shields.io/badge/Platform-Android-green.svg?style=flat)

Selamat datang di repositori CinemaZone! Ini adalah aplikasi pemesanan tiket bioskop berbasis Android yang dikembangkan sebagai Proyek Akhir. Aplikasi ini dirancang untuk memberikan pengalaman pemesanan tiket yang mulus, mulai dari melihat daftar film, memesan kursi, hingga mendapatkan e-ticket dalam bentuk QR Code.

## 📸 Screenshot Aplikasi

_**Catatan:** Ganti link di bawah ini dengan screenshot nyata dari aplikasi Anda. Anda bisa mengunggah gambar ke tab "Issues" di GitHub untuk mendapatkan link, lalu menyalin alamat gambarnya._

| Halaman Login | Halaman Utama (Home) | Detail Film |
| :---: | :---: | :---: |
| ![Login](https://via.placeholder.com/250x500.png?text=Halaman+Login) | ![Home](https://via.placeholder.com/250x500.png?text=Halaman+Utama) | ![Detail](https://via.placeholder.com/250x500.png?text=Detail+Film) |

| Pemilihan Kursi | Riwayat Pesanan | E-Ticket QR Code |
| :---: | :---: | :---: |
| ![Pilih Kursi](https://via.placeholder.com/250x500.png?text=Pilih+Kursi) | ![Riwayat](https://via.placeholder.com/250x500.png?text=Riwayat+Pesanan) | ![E-Ticket](https://via.placeholder.com/250x500.png?text=E-Ticket+QR) |


## ✨ Fitur Utama

Aplikasi CinemaZone dilengkapi dengan berbagai fitur modern, antara lain:

* **Autentikasi Pengguna:**
    * Registrasi dan Login pengguna menggunakan **Firebase Authentication**.
    * Manajemen sesi yang aman (pengguna tetap login setelah aplikasi ditutup).

* **Katalog Film:**
    * Menampilkan daftar film yang sedang tayang dari sumber data JSON.
    * Klik pada film untuk melihat deskripsi lengkap dan menonton trailer (membuka YouTube/Browser).

* **Pemesanan Tiket:**
    * Mekanisme pemesanan dengan memilih jumlah tiket.
    * **Peta Kursi Visual** yang interaktif untuk memilih kursi.
    * Validasi untuk memastikan jumlah kursi yang dipilih sesuai dengan jumlah tiket.
    * Menyimpan data pesanan ke database **SQLite** lokal.

* **E-Ticket & QR Code:**
    * Membuat **E-Ticket** untuk setiap pesanan yang berhasil.
    * Generate **QR Code** unik yang berisi detail pesanan dalam format JSON.
    * Fitur untuk **menyimpan E-Ticket** sebagai gambar ke galeri perangkat.

* **Manajemen Akun & Profil:**
    * Halaman profil yang menampilkan data pengguna (nama & email) dari Firebase.
    * Fitur **Ubah Nama** yang tersimpan di profil Firebase.
    * Fitur **Riwayat Pesanan** untuk melihat semua tiket yang pernah dipesan.
    * Tombol Logout yang aman.

* **Fitur Tambahan:**
    * **Wishlist** untuk menyimpan film yang ingin ditonton (menggunakan database SQLite).
    * Halaman "Tentang Aplikasi".
    * Desain UI yang bersih menggunakan Material Design 3.

## 🛠️ Teknologi & Library yang Digunakan

Proyek ini dibangun menggunakan teknologi dan library modern dalam ekosistem Android:

* **Bahasa Pemrograman:** [Kotlin](https://kotlinlang.org/)
* **Arsitektur:** Single-Activity Architecture, Manual Fragment Management
* **UI:** XML Layouts, [Material Design 3](https://m3.material.io/)
* **Asynchronous:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) (`lifecycleScope`)
* **Networking:**
    * [Retrofit 2](https://square.github.io/retrofit/): Untuk komunikasi dengan API/sumber data JSON.
    * [OkHttp 4](https://square.github.io/okhttp/): Sebagai HTTP client untuk Retrofit.
    * [Gson](https://github.com/google/gson): Untuk parsing JSON ke objek Kotlin.
* **Database Lokal:** [SQLite](https://www.sqlite.org/index.html) (melalui `SQLiteOpenHelper`) untuk menyimpan data Wishlist dan Riwayat Pesanan.
* **Autentikasi:** [Firebase Authentication](https://firebase.google.com/docs/auth)
* **Image Loading:** [Glide](https://github.com/bumptech/glide)
* **QR Code Generation:** [ZXing (Zebra Crossing)](https://github.com/zxing/zxing)

## 🚀 Setup & Instalasi

Untuk menjalankan proyek ini di Android Studio, ikuti langkah-langkah berikut:

1.  **Clone Repositori**
    ```bash
    git clone [https://github.com/](https://github.com/)[NamaUserGitHubAnda]/[NamaRepoAnda].git
    ```
2.  **Buka di Android Studio**
    * Buka Android Studio.
    * Pilih "Open" dan arahkan ke folder proyek yang baru saja di-clone.
    * Tunggu hingga Gradle selesai melakukan sinkronisasi.

3.  **PENTING: Setup Firebase (`google-services.json`)**
    Repositori ini sengaja **tidak menyertakan** file `google-services.json` untuk alasan keamanan. Anda harus membuat proyek Firebase Anda sendiri untuk mendapatkan file ini.

    * Pergi ke [Firebase Console](https://console.firebase.google.com/) dan buat proyek baru.
    * Tambahkan aplikasi Android baru ke dalam proyek Firebase tersebut.
    * Ikuti instruksi untuk mendaftarkan aplikasi (Anda akan memerlukan **package name** dan **SHA-1 certificate fingerprint** dari proyek ini).
    * Unduh file `google-services.json` yang diberikan oleh Firebase.
    * Salin file `google-services.json` tersebut ke dalam direktori **`app/`** pada proyek Android Studio Anda.
    * Di Firebase Console, jangan lupa untuk mengaktifkan **Email/Password sign-in method** di bagian **Authentication**.

4.  **Build dan Jalankan Aplikasi**
    * Setelah file `google-services.json` ada di tempat yang benar, Anda bisa langsung menekan tombol **Run 'app'** (ikon play hijau) di Android Studio.

## 🧑‍💻 Kontributor

Proyek ini disusun untuk Proyek Akhir Mata Kuliah Pemrograman Berbasis Platform dan dibuat oleh:

* **[Rafie Rojagat Bachri / Kelompok 5]**

---

Terima kasih telah mengunjungi repositori ini!
