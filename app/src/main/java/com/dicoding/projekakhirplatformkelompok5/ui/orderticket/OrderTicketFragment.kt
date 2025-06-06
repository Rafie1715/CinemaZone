package com.dicoding.projekakhirplatformkelompok5.ui.orderticket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.projekakhirplatformkelompok5.R
import com.dicoding.projekakhirplatformkelompok5.data.local.Order
import com.dicoding.projekakhirplatformkelompok5.data.local.OrderDatabaseHelper
import com.dicoding.projekakhirplatformkelompok5.data.network.ApiClient
import com.dicoding.projekakhirplatformkelompok5.databinding.FragmentOrderTicketBinding
import com.dicoding.projekakhirplatformkelompok5.ui.profile.TicketDetailActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderTicketFragment : Fragment() {

    private var _binding: FragmentOrderTicketBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: OrderDatabaseHelper
    private var availableMoviesTitle = mutableListOf<String>()
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private var selectedSeats = listOf<String>()

    // Instance Firebase Auth sudah dideklarasikan di sini
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(SeatPickerDialogFragment.REQUEST_KEY) { _, bundle ->
            val result = bundle.getStringArrayList(SeatPickerDialogFragment.RESULT_SEATS)
            result?.let {
                selectedSeats = it
                binding.tvSelectedSeats.text = "Kursi Dipilih: ${it.joinToString(", ")}"
                binding.tvSelectedSeats.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderTicketBinding.inflate(inflater, container, false)
        dbHelper = OrderDatabaseHelper(requireContext())
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSelectedSeats.visibility = View.GONE
        setupOrderHistoryRecyclerView()
        setupMovieSpinner()
        fetchMoviesForSpinner()

        binding.btnSelectSeats.setOnClickListener {
            val quantityText = binding.etQuantity.text.toString()
            val quantity = quantityText.toIntOrNull() ?: 0

            if (quantity <= 0) {
                Toast.makeText(requireContext(), "Silakan masukkan jumlah tiket terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            SeatPickerDialogFragment.newInstance(quantity)
                .show(parentFragmentManager, SeatPickerDialogFragment.TAG)
        }

        binding.btnProcessOrder.setOnClickListener {
            processOrder()
        }
        loadOrderHistory()
    }

    // Fungsi fetchMoviesForSpinner dan setup lainnya tidak perlu diubah
    private fun setupMovieSpinner() {
        val movieAdapterSpinner = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf(getString(R.string.choose_film_prompt))
        )
        movieAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMovieSelection.adapter = movieAdapterSpinner
    }

    // ... (Fungsi setupOrderHistoryRecyclerView dan fetchMoviesForSpinner tetap sama) ...
    private fun setupOrderHistoryRecyclerView() {
        orderHistoryAdapter = OrderHistoryAdapter(emptyList()) { selectedOrder ->
            // 1. Buat Intent untuk membuka halaman detail tiket
            val intent = Intent(activity, TicketDetailActivity::class.java)

            // 2. Selipkan data pesanan yang di-klik ke dalam Intent
            intent.putExtra(TicketDetailActivity.EXTRA_ORDER, selectedOrder)

            // 3. Mulai Activity baru
            startActivity(intent)
        }
        binding.recyclerViewOrderHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderHistoryAdapter
        }
    }

    private fun fetchMoviesForSpinner() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.instance.getAllMoviesDirect()
                if (response.isSuccessful) {
                    response.body()?.let { movies ->
                        availableMoviesTitle.clear()
                        availableMoviesTitle.add(getString(R.string.choose_film_prompt))
                        availableMoviesTitle.addAll(movies.map { it.title })
                        val movieSpinnerAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            availableMoviesTitle
                        )
                        movieSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerMovieSelection.adapter = movieSpinnerAdapter
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat daftar film", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error memuat film: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * REVISI: Menggunakan Firebase Auth untuk mendapatkan ID pengguna.
     */
    private fun processOrder() {
        val selectedMovieTitle = binding.spinnerMovieSelection.selectedItem.toString()
        val quantityText = binding.etQuantity.text.toString()
        val quantity = quantityText.toIntOrNull() ?: 0

        // Validasi
        if (selectedMovieTitle == getString(R.string.choose_film_prompt)) { /* ... */ return }
        if (quantity <= 0) { /* ... */ return }
        if (selectedSeats.isEmpty() || selectedSeats.size != quantity) { /* ... */ return }

        // REVISI: Dapatkan pengguna dari Firebase Auth
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Sesi tidak valid, silakan login kembali.", Toast.LENGTH_SHORT).show()
            return
        }
        val userEmail = user.email!! // Gunakan email dari Firebase sebagai ID

        // Proses penyimpanan ke DB
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDateAndTime: String = sdf.format(Date())
        val seatsString = selectedSeats.joinToString(", ")

        val order = Order(
            userId = userEmail,
            movieTitle = selectedMovieTitle,
            quantity = quantity,
            seat = seatsString,
            orderDate = currentDateAndTime
        )

        val successId = dbHelper.addOrder(order)
        if (successId > -1) {
            Toast.makeText(requireContext(), "Tiket berhasil dipesan!", Toast.LENGTH_LONG).show()
            // Reset form
            binding.spinnerMovieSelection.setSelection(0)
            binding.etQuantity.text.clear()
            binding.tvSelectedSeats.text = ""
            binding.tvSelectedSeats.visibility = View.GONE
            selectedSeats = emptyList()

            loadOrderHistory()
        } else {
            Toast.makeText(requireContext(), "Gagal memesan tiket.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * REVISI: Menggunakan Firebase Auth untuk mendapatkan ID pengguna.
     */
    private fun loadOrderHistory() {
        // REVISI: Dapatkan pengguna dari Firebase Auth
        val user = auth.currentUser

        if (user != null) {
            val email = user.email!!
            val orders = dbHelper.getAllOrdersByUser(email)
            if (orders.isNotEmpty()) {
                orderHistoryAdapter.updateOrders(orders)
                binding.recyclerViewOrderHistory.visibility = View.VISIBLE
                binding.tvNoOrderHistory.visibility = View.GONE
            } else {
                orderHistoryAdapter.updateOrders(emptyList())
                binding.recyclerViewOrderHistory.visibility = View.GONE
                binding.tvNoOrderHistory.visibility = View.VISIBLE
                binding.tvNoOrderHistory.text = "Belum ada riwayat pesanan."
            }
        } else {
            // Jika user null (tidak login)
            orderHistoryAdapter.updateOrders(emptyList())
            binding.recyclerViewOrderHistory.visibility = View.GONE
            binding.tvNoOrderHistory.visibility = View.VISIBLE
            binding.tvNoOrderHistory.text = "Login untuk melihat riwayat pesanan."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}