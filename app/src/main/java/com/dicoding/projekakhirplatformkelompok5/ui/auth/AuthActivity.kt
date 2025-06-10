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

        if (auth.currentUser != null) {
            navigateToMain()
            return
        }

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.auth_fragment_container, LoginFragment())
                .commitNow()
        }
    }

    fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun navigateToRegister() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    fun navigateToLogin() {
        supportFragmentManager.popBackStack()
    }
}