package com.dicoding.projekakhirplatformkelompok5.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.projekakhirplatformkelompok5.data.local.Order
import com.dicoding.projekakhirplatformkelompok5.databinding.ActivityOrderHistoryBinding
import com.dicoding.projekakhirplatformkelompok5.ui.orderticket.OrderHistoryAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarOrderHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
        val user = auth.currentUser
        if (user == null) {
            showEmptyState("Login untuk melihat riwayat.")
            return
        }

        db.collection("users").document(user.uid).collection("orders")
            .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (false) return@addOnSuccessListener

                val orderList = mutableListOf<Order>()
                for (document in result.documents) { // Lebih aman menggunakan result.documents
                    // REVISI DI SINI
                    val order = document.toObject(Order::class.java)

                    // Set ID objek dengan ID dokumen dari Firestore
                    order?.id = document.id
                    if (order != null) {
                        orderList.add(order)
                    }
                }

                if (orderList.isNotEmpty()) {
                    orderHistoryAdapter.updateOrders(orderList)
                    binding.recyclerViewOrderHistoryPage.visibility = View.VISIBLE
                    binding.tvNoOrderHistoryPage.visibility = View.GONE
                } else {
                    showEmptyState("Belum ada riwayat pesanan.")
                }
            }
            .addOnFailureListener { exception ->
                showEmptyState("Gagal memuat riwayat.")
                Log.e("OrderHistoryActivity", "Gagal memuat pesanan", exception)
            }
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