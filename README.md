# CinemaZone 🎬✨

![Kotlin](https://img.shields.io/badge/Made%20with-Kotlin-blue?logo=kotlin)
![Platform](https://img.shields.io/badge/Platform-Android-green.svg?style=flat)
![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)

Selamat datang di repositori **CinemaZone**, sebuah aplikasi pemesanan tiket bioskop modern berbasis Android yang dikembangkan sebagai Proyek Akhir. Aplikasi ini dirancang dengan antarmuka yang dinamis dan fungsionalitas lengkap, memberikan pengalaman pengguna yang mulus dari awal hingga akhir.

Aplikasi ini mendemonstrasikan implementasi arsitektur Android modern, integrasi dengan layanan backend, manajemen database lokal, dan peningkatan UX melalui animasi.

## 📸 Screenshot Aplikasi

_**Catatan:** Ganti link placeholder di bawah ini dengan URL screenshot nyata dari aplikasi Anda._

| Halaman Login & Register | Halaman Utama (Home) | Filter & Search |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/e8f383b7-8884-498e-9eb8-1827428fbc76" alt="Login" width="270"> <br><br> <img src="https://github.com/user-attachments/assets/b9842457-26c5-4f3e-a7aa-a2beb8b5f9e9" alt="Register" width="270"> | ![Home](https://github.com/user-attachments/assets/977818f9-a481-4447-83d1-2edd14260b21) | ![Filter](https://github.com/user-attachments/assets/c70d1276-96d9-4ca0-a1f3-6aacc76fd4cf) |

| Detail Film | Pemesanan & Pemilihan Kursi | Riwayat Pesanan|
| :---: | :---: | :---: |
| ![Detail Film](https://github.com/user-attachments/assets/4da9f404-9f31-4d4b-9118-8e3b0020780d) | <img src="https://github.com/user-attachments/assets/27063fb4-2290-48fa-b123-ddd474a7dec5" alt="Pemesanan Tiker" width="270"> <br><br> <img src="https://github.com/user-attachments/assets/c2aa9ef4-e9d9-4062-9020-24a81a4ac8e2" alt="Pemilihan Kursi" width="270">  | ![E-Ticket](https://github.com/user-attachments/assets/de64b547-69d0-4330-a552-9ccbb21a6138) |

| Halaman Wishlist | Halaman Profil | E-Ticket |
| :---: | :---: | :---: |
| ![Wishlist](https://github.com/user-attachments/assets/bb94a838-3792-4e65-a706-f2def6ce7cae) | ![Profil](https://github.com/user-attachments/assets/a98407f8-9f20-4729-b130-754b5ab648d2) | ![Riwayat Pesanan](https://github.com/user-attachments/assets/a2093950-3590-46fd-b5f2-9bfb5709b139) |


## ✨ Fitur Utama

* **Autentikasi Pengguna Modern:**
    * Registrasi dan Login pengguna yang aman menggunakan **Firebase Authentication**.
    * Manajemen sesi otomatis, pengguna tetap login setelah aplikasi ditutup.
    * Fitur update nama profil yang tersimpan di Firebase.

* **Katalog Film Dinamis:**
    * Menampilkan daftar film dari sumber data JSON eksternal.
    * **Search Bar** untuk mencari film berdasarkan judul secara *real-time*.
    * **Filter Kategori Genre** menggunakan `ChipGroup` yang dinamis.
    * Tampilan detail film dalam dialog modern saat item diklik, lengkap dengan tombol untuk menonton trailer.

* **Alur Pemesanan Tiket Lengkap:**
    * Form pemesanan bertingkat yang logis: Pilih Film -> Lokasi -> Tanggal -> Jam Tayang.
    * Pilihan yang saling bergantung (misal: pilihan tanggal muncul setelah lokasi dipilih).
    * **Peta Kursi Visual** interaktif yang memungkinkan pengguna memilih kursi secara manual.
    * Validasi untuk memastikan jumlah tiket sesuai dengan jumlah kursi yang dipilih.

* **E-Ticket & QR Code:**
    * Membuat **E-Ticket** digital untuk setiap pesanan yang berhasil.
    * Generate **QR Code** unik yang berisi seluruh detail pesanan (dalam format JSON).
    * Fitur untuk **menyimpan E-Ticket** sebagai gambar ke galeri perangkat pengguna.

* **Manajemen Data Lokal & Akun:**
    * **Wishlist:** Menyimpan film favorit secara lokal per pengguna menggunakan database **SQLite**.
    * **Riwayat Pesanan:** Semua transaksi tiket disimpan di database **SQLite** dan dapat diakses melalui halaman profil.
    * Halaman profil yang menampilkan data pengguna, serta menyediakan akses ke Riwayat Pesanan dan halaman Tentang Aplikasi.

* **Pengalaman Pengguna (UX) yang Ditingkatkan:**
    * **Animasi RecyclerView:** Item film muncul dengan efek *slide-up* dan *fade-in* yang elegan.
    * **Animasi Interaktif:** Ikon "Wishlist" memberikan feedback animasi "pop" saat ditekan.
    * **Transisi Halus:** Perpindahan antar halaman utama (Home, Order, dll.) menggunakan animasi *fade* yang lembut.
    * Desain UI yang bersih dan modern mengadopsi prinsip-prinsip Material Design 3.

## 🛠️ Teknologi & Library yang Digunakan

* **Bahasa Pemrograman:** [Kotlin](https://kotlinlang.org/)
* **Arsitektur:** Single-Activity Architecture, Manajemen Fragment Manual (`FragmentManager`)
* **UI:** XML Layouts, [Material Design 3](https://m3.material.io/)
* **Asynchronous:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) (`lifecycleScope`)
* **Networking:**
    * [Retrofit 2](https://square.github.io/retrofit/): HTTP Client untuk mengambil data dari API/JSON.
    * [Gson](https://github.com/google/gson): Parsing JSON menjadi objek Kotlin.
* **Database Lokal:** [SQLite](https://www.sqlite.org/index.html) (melalui `SQLiteOpenHelper`)
* **Autentikasi:** [Firebase Authentication](https://firebase.google.com/docs/auth)
* **Image Loading:** [Glide](https://github.com/bumptech/glide)
* **QR Code Generation:** [ZXing (Zebra Crossing) Core](https://github.com/zxing/zxing)
* **Animasi:** `ViewPropertyAnimator` dan `ObjectAnimator`.

## 🚀 Setup & Instalasi

Untuk menjalankan proyek ini di Android Studio, ikuti langkah-langkah berikut:

1.  **Clone Repositori**
    ```bash
    git clone [https://github.com/](https://github.com/)[NamaUserGitHubAnda]/[NamaRepoAnda].git
    ```
2.  **Buka di Android Studio**
    * Buka Android Studio, pilih "Open", dan arahkan ke folder proyek.
    * Tunggu hingga proses Gradle Sync selesai.

3.  **PENTING: Setup Firebase (`google-services.json`)**
    Repositori ini sengaja **tidak menyertakan** file `google-services.json`. Anda harus membuat proyek Firebase Anda sendiri.

    * Pergi ke [Firebase Console](https://console.firebase.google.com/) dan buat proyek baru.
    * Tambahkan aplikasi Android, masukkan **package name** dan **SHA-1 certificate fingerprint** dari komputer Anda.
    * Unduh file `google-services.json` yang dihasilkan.
    * Salin file tersebut ke dalam direktori **`app/`** pada proyek Android Studio Anda.
    * Di Firebase Console, aktifkan **Email/Password sign-in method** di bagian **Authentication**.

4.  **Siapkan Data Film (`movies.json`)**
    * Aplikasi ini mengambil data dari sebuah URL file `movies.json`.
    * Buka file `app/src/main/java/.../data/network/ApiService.kt`.
    * Ubah URL placeholder di dalam anotasi `@GET` dengan URL raw dari file `movies.json` Anda sendiri yang sudah di-hosting (misalnya di GitHub Gist).

5.  **Build dan Jalankan Aplikasi**
    * Setelah `google-services.json` dan URL API sudah benar, klik tombol **Run 'app'** (ikon play hijau) di Android Studio.
    * **Catatan:** Jika Anda mengalami error terkait database saat pertama kali menjalankan, coba **uninstall** aplikasi dari emulator/perangkat, lalu jalankan lagi. Ini untuk memastikan skema database SQLite yang baru dibuat dengan benar.

## 🧑‍💻 Kontributor

Proyek ini disusun untuk Proyek Akhir Mata Kuliah Pemrograman Berbasis Platform dan dibuat oleh:

* **[Rafie Rojagat Bachri / Kelompok 5]**

---

Terima kasih telah mengunjungi repositori ini!
