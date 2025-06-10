package com.dicoding.projekakhirplatformkelompok5.ui.profile

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.projekakhirplatformkelompok5.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAbout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        try {
            val version = packageManager.getPackageInfo(packageName, 0).versionName
            binding.tvAppVersion.text = "Versi $version"
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvAppVersion.text = "Versi 1.0.0"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}