# CinemaZone 🎬✨

# Proyek ini disusun untuk Proyek Akhir Mata Kuliah Pemrograman Berbasis Platform

![Kotlin](https://img.shields.io/badge/Made%20with-Kotlin-blue?logo=kotlin)
![Platform](https://img.shields.io/badge/Platform-Android-green.svg?style=flat)
![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)

Selamat datang di repositori **CinemaZone**, sebuah aplikasi pemesanan tiket bioskop modern berbasis Android yang dikembangkan sebagai Proyek Akhir. Aplikasi ini dirancang dengan antarmuka yang dinamis dan fungsionalitas lengkap, memberikan pengalaman pengguna yang mulus dari awal hingga akhir.

Aplikasi ini mendemonstrasikan implementasi arsitektur Android modern, integrasi dengan layanan backend, manajemen database lokal, dan peningkatan UX melalui animasi.

## 📸 Screenshot Aplikasi

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

* **Autentikasi Pengguna Modern:** Registrasi dan Login pengguna yang aman menggunakan **Firebase Authentication**.
* **Katalog Film Dinamis:** Menampilkan daftar film dengan **Search Bar** dan **Filter Kategori Genre**.
* **Alur Pemesanan Tiket Lengkap:** Form pemesanan bertingkat (Film, Lokasi, Jadwal) dengan **Peta Kursi Visual** yang interaktif.
* **E-Ticket & QR Code:** Membuat E-Ticket dengan QR Code unik yang bisa **disimpan ke galeri**.
* **Manajemen Akun & Data:** **Wishlist** dan **Riwayat Pesanan** yang tersimpan per pengguna di **Cloud Firestore**.
* **Pengalaman Pengguna (UX) Ditingkatkan:** Animasi pada daftar film, ikon interaktif, dan transisi antar halaman yang halus.

## 🛠️ Teknologi & Library yang Digunakan

* **Bahasa Pemrograman:** [Kotlin](https://kotlinlang.org/)
* **Arsitektur:** Single-Activity Architecture, Manajemen Fragment Manual
* **UI:** XML Layouts, [Material Design 3](https://m3.material.io/)
* **Asynchronous:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
* **Networking:** [Retrofit 2](https://square.github.io/retrofit/), [Gson](https://github.com/google/gson)
* **Database Online:** [Cloud Firestore](https://firebase.google.com/docs/firestore)
* **Autentikasi:** [Firebase Authentication](https://firebase.google.com/docs/auth)
* **Image Loading:** [Glide](https://github.com/bumptech/glide)
* **QR Code Generation:** [ZXing (Zebra Crossing) Core](https://github.com/zxing/zxing)
* **Animasi:** `ViewPropertyAnimator` & `ObjectAnimator`.

## 🚀 Setup & Instalasi

Untuk menjalankan proyek ini di Android Studio, ikuti langkah-langkah berikut:

1.  **Clone Repositori**
    ```bash
    git clone https://github.com/Rafie1715/CinemaZone.git
    ```
2.  **Buka di Android Studio** dan tunggu proses Gradle Sync.

3.  **Setup Firebase (`google-services.json`)**
    * Proyek ini memerlukan file `google-services.json` dari proyek Firebase Anda sendiri.
    * Daftarkan aplikasi Anda di Firebase Console, unduh filenya, dan letakkan di dalam direktori **`app/`**.
    * Aktifkan **Email/Password** di **Authentication** dan buat **Cloud Firestore** dalam **Test Mode**.

4.  **Siapkan Data Film (`movies.json`)**
    * Buka file `app/src/main/java/.../data/network/ApiService.kt`.
    * Ubah URL placeholder di dalam anotasi `@GET` dengan URL raw dari file `movies.json` Anda.

5.  **Build dan Jalankan Aplikasi**.yang baru dibuat dengan benar.
