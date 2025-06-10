package com.dicoding.projekakhirplatformkelompok5.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.projekakhirplatformkelompok5.data.local.OrderDatabaseHelper
import com.dicoding.projekakhirplatformkelompok5.databinding.ActivityOrderHistoryBinding
import com.dicoding.projekakhirplatformkelompok5.ui.orderticket.OrderHistoryAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var dbHelper: OrderDatabaseHelper
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarOrderHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = OrderDatabaseHelper(this)

        setupRecyclerView()
        loadOrderHistory()
    }

    private fun setupRecyclerView() {
        orderHistoryAdapter = OrderHistoryAdapter(emptyList()) { selectedOrder ->
            val intent = Intent(this, TicketDetailActivity::class.java)
            intent.putExtra(TicketDetailActivity.EXTRA_ORDER, selectedOrder)
            startActivity(intent)
        }
        binding.recyclerViewOrderHistoryPage.adapter = orderHistoryAdapter
    }

    private fun loadOrderHistory() {
        val user = Firebase.auth.currentUser
        user?.let {
            val orders = dbHelper.getAllOrdersByUser(it.uid)
            if (orders.isNotEmpty()) {
                orderHistoryAdapter.updateOrders(orders)
                binding.recyclerViewOrderHistoryPage.visibility = View.VISIBLE
                binding.tvNoOrderHistoryPage.visibility = View.GONE
            } else {
                showEmptyState("Belum ada riwayat pesanan.")
            }
        } ?: showEmptyState("Login untuk melihat riwayat.")
    }

    private fun showEmptyState(message: String) {
        binding.recyclerViewOrderHistoryPage.visibility = View.GONE
        binding.tvNoOrderHistoryPage.visibility = View.VISIBLE
        binding.tvNoOrderHistoryPage.text = message
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}