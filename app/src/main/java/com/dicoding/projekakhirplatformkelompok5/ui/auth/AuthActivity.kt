package com.dicoding.projekakhirplatformkelompok5.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dicoding.projekakhirplatformkelompok5.MainActivity
import com.dicoding.projekakhirplatformkelompok5.R
import com.dicoding.projekakhirplatformkelompok5.databinding.ActivityAuthBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        // Cek status login dari Firebase
        if (auth.currentUser != null) {
            navigateToMain()
            return
        }

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) { // Hanya tampilkan fragment jika activity baru dibuat
            supportFragmentManager.beginTransaction()
                .replace(R.id.auth_fragment_container, LoginFragment())
                .commitNow()
        }
    }

    fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Tutup AuthActivity agar tidak bisa kembali dengan tombol back
    }

    fun navigateToRegister() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container, RegisterFragment())
            .addToBackStack(null) // Agar bisa kembali ke LoginFragment
            .commit()
    }

    fun navigateToLogin() {
        supportFragmentManager.popBackStack() // Kembali ke fragment sebelumnya (LoginFragment)
    }

    private fun isUserLoggedIn(): Boolean {
        // Implementasi pengecekan status login
        val sharedPreferences = getSharedPreferences("CinemaZonePrefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
}