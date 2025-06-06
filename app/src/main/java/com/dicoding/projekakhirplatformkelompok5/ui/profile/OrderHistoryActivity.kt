package com.dicoding.projekakhirplatformkelompok5.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.projekakhirplatformkelompok5.R
import com.dicoding.projekakhirplatformkelompok5.data.local.OrderDatabaseHelper
import com.dicoding.projekakhirplatformkelompok5.databinding.ActivityOrderHistoryBinding
import com.dicoding.projekakhirplatformkelompok5.ui.orderticket.OrderHistoryAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var dbHelper: OrderDatabaseHelper
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbarOrderHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Tampilkan tombol back

        // Inisialisasi semua helper dan properti di sini
        dbHelper = OrderDatabaseHelper(this)
        auth = Firebase.auth // <-- REVISI: Tambahkan baris ini untuk menginisialisasi auth

        setupRecyclerView()
        loadOrderHistory()
    }

    private fun setupRecyclerView() {
        orderHistoryAdapter = OrderHistoryAdapter(emptyList()) { selectedOrder ->
            // Saat item diklik, buka TicketDetailActivity
            val intent = Intent(this, TicketDetailActivity::class.java)
            intent.putExtra(TicketDetailActivity.EXTRA_ORDER, selectedOrder)
            startActivity(intent)
        }
        binding.recyclerViewOrderHistoryPage.adapter = orderHistoryAdapter
    }

    private fun loadOrderHistory() {
        // Ambil pengguna dari Firebase Auth
        val user = auth.currentUser

        if (user != null) {
            val email = user.email!! // Gunakan email dari Firebase
            val orders = dbHelper.getAllOrdersByUser(email)

            if (orders.isNotEmpty()) {
                orderHistoryAdapter.updateOrders(orders)
                binding.recyclerViewOrderHistoryPage.visibility = View.VISIBLE
                binding.tvNoOrderHistoryPage.visibility = View.GONE
            } else {
                showEmptyState("Belum ada riwayat pesanan.")
            }
        } else {
            showEmptyState("Login untuk melihat riwayat pesanan.")
        }
    }

    private fun showEmptyState(message: String) {
        binding.recyclerViewOrderHistoryPage.visibility = View.GONE
        binding.tvNoOrderHistoryPage.visibility = View.VISIBLE
        binding.tvNoOrderHistoryPage.text = message
    }

    // Handle klik tombol back di toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}