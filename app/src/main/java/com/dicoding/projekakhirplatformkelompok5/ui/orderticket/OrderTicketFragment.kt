package com.dicoding.projekakhirplatformkelompok5.ui.orderticket

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.projekakhirplatformkelompok5.data.local.Order
import com.dicoding.projekakhirplatformkelompok5.data.local.OrderDatabaseHelper
import com.dicoding.projekakhirplatformkelompok5.data.model.Movie
import com.dicoding.projekakhirplatformkelompok5.data.model.Show
import com.dicoding.projekakhirplatformkelompok5.data.model.Showtime
import com.dicoding.projekakhirplatformkelompok5.data.model.TimeSlot
import com.dicoding.projekakhirplatformkelompok5.data.network.ApiClient
import com.dicoding.projekakhirplatformkelompok5.databinding.FragmentOrderTicketBinding
import com.dicoding.projekakhirplatformkelompok5.ui.profile.TicketDetailActivity
import com.google.android.material.chip.Chip
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderTicketFragment : Fragment() {

    private var _binding: FragmentOrderTicketBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: OrderDatabaseHelper
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var auth: FirebaseAuth

    private var fullMovieList = listOf<Movie>()
    private var selectedMovie: Movie? = null
    private var selectedShowtime: Showtime? = null
    private var selectedShow: Show? = null
    private var selectedTimeSlot: TimeSlot? = null
    private var selectedSeats = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderTicketBinding.inflate(inflater, container, false)
        dbHelper = OrderDatabaseHelper(requireContext())
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(SeatPickerDialogFragment.REQUEST_KEY) { _, bundle ->
            val result = bundle.getStringArrayList(SeatPickerDialogFragment.RESULT_SEATS)
            result?.let {
                selectedSeats = it
                binding.tvSelectedSeats.text = "Kursi Dipilih: ${it.joinToString(", ")}"
                binding.tvSelectedSeats.visibility = View.VISIBLE
            }
        }

        resetAllViews()
        setupOrderHistoryRecyclerView()
        fetchMoviesForSpinner()

        binding.btnSelectSeats.setOnClickListener {
            val quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 0
            if (quantity > 0) {
                SeatPickerDialogFragment.newInstance(quantity).show(parentFragmentManager, SeatPickerDialogFragment.TAG)
            } else {
                Toast.makeText(requireContext(), "Masukkan jumlah tiket terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnProcessOrder.setOnClickListener { processOrder() }
        loadOrderHistory()
    }

    private fun resetAllViews(step: Int = 0) {
        binding.layoutSpinnerLocation.visibility = if (step >= 1) View.VISIBLE else View.GONE
        binding.layoutSpinnerDate.visibility = if (step >= 2) View.VISIBLE else View.GONE
        binding.layoutTimeSelection.visibility = if (step >= 3) View.VISIBLE else View.GONE
        binding.cardOrderSummary.visibility = if (step >= 4) View.VISIBLE else View.GONE
        binding.layoutSeatSelection.visibility = if (step >= 4) View.VISIBLE else View.GONE
        binding.btnProcessOrder.visibility = if (step >= 4) View.VISIBLE else View.GONE

        if (step < 1) { selectedMovie = null; if(view != null) binding.spinnerLocation.setText("", false) }
        if (step < 2) { selectedShowtime = null; if(view != null) binding.spinnerDate.setText("", false) }
        if (step < 3) { selectedShow = null; binding.chipGroupTime.removeAllViews() }
        if (step < 4) {
            selectedTimeSlot = null
            binding.etQuantity.text?.clear()
            binding.tvSelectedSeats.text = ""
            binding.tvSelectedSeats.visibility = View.GONE
            selectedSeats = emptyList()
        }
    }

    private fun fetchMoviesForSpinner() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.instance.getAllMovies()
                if (response.isSuccessful) {
                    fullMovieList = response.body() ?: emptyList()
                    val movieTitles = mutableListOf("Pilih Film")
                    movieTitles.addAll(fullMovieList.map { it.title })
                    val movieAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, movieTitles)
                    binding.spinnerMovie.setAdapter(movieAdapter)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal memuat film: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.spinnerMovie.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            resetAllViews()
            if (position > 0) {
                selectedMovie = fullMovieList[position - 1]
                updateLocationSpinner()
            }
        }
    }

    private fun updateLocationSpinner() {
        resetAllViews(step = 1)
        val locations = mutableListOf("Pilih Lokasi")
        selectedMovie?.showtimes?.let { showtimes ->
            locations.addAll(showtimes.map { it.location })
        }
        val locationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, locations)
        binding.spinnerLocation.setAdapter(locationAdapter)
        binding.spinnerLocation.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            resetAllViews(step = 1)
            if (position > 0) {
                selectedShowtime = selectedMovie?.showtimes?.get(position - 1)
                updateDateSpinner()
            }
        }
    }

    private fun updateDateSpinner() {
        resetAllViews(step = 2)
        val dates = mutableListOf("Pilih Tanggal")
        selectedShowtime?.shows?.let { shows ->
            dates.addAll(shows.map { it.date })
        }
        val dateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, dates)
        binding.spinnerDate.setAdapter(dateAdapter)
        binding.spinnerDate.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            resetAllViews(step = 2)
            if (position > 0) {
                selectedShow = selectedShowtime?.shows?.get(position - 1)
                updateTimeChips()
            }
        }
    }

    private fun updateTimeChips() {
        resetAllViews(step = 3)
        binding.chipGroupTime.removeAllViews()
        selectedShow?.times?.forEach { timeSlot ->
            val chip = Chip(context).apply {
                text = timeSlot.time
                isCheckable = true
                isClickable = true
            }
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedTimeSlot = timeSlot
                    updateOrderSummary()
                    resetAllViews(step = 4)
                }
            }
            binding.chipGroupTime.addView(chip)
        }
    }

    private fun updateOrderSummary() {
        binding.tvStudioInfo.text = "Studio: ${selectedTimeSlot?.studio}"
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        format.maximumFractionDigits = 0
        binding.tvPriceInfo.text = "Harga per Tiket: ${format.format(selectedShowtime?.price)}"
    }

    private fun processOrder() {
        val user = auth.currentUser
        val quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 0
        if (user == null || selectedMovie == null || selectedShowtime == null || selectedShow == null || selectedTimeSlot == null || quantity <= 0 || selectedSeats.size != quantity) {
            Toast.makeText(requireContext(), "Harap lengkapi semua pilihan dengan benar.", Toast.LENGTH_LONG).show()
            return
        }

        val order = Order(
            userId = user.uid,
            movieTitle = selectedMovie!!.title,
            quantity = quantity,
            seat = selectedSeats.joinToString(", "),
            orderDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            location = selectedShowtime!!.location,
            showDate = selectedShow!!.date,
            showTime = selectedTimeSlot!!.time,
            studio = selectedTimeSlot!!.studio,
            pricePerTicket = selectedShowtime!!.price
        )

        val successId = dbHelper.addOrder(order)
        if (successId > -1) {
            Toast.makeText(requireContext(), "Tiket berhasil dipesan!", Toast.LENGTH_LONG).show()
            binding.spinnerMovie.setText("", false)
            resetAllViews()
            loadOrderHistory()
        } else {
            Toast.makeText(requireContext(), "Gagal menyimpan pesanan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupOrderHistoryRecyclerView() {
        orderHistoryAdapter = OrderHistoryAdapter(emptyList()) { selectedOrder ->
            val intent = Intent(activity, TicketDetailActivity::class.java)
            intent.putExtra(TicketDetailActivity.EXTRA_ORDER, selectedOrder)
            startActivity(intent)
        }
        binding.recyclerViewOrderHistory.adapter = orderHistoryAdapter
        binding.recyclerViewOrderHistory.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadOrderHistory() {
        val user = auth.currentUser
        if (user != null) {
            val orders = dbHelper.getAllOrdersByUser(user.uid)
            if (orders.isNotEmpty()) {
                orderHistoryAdapter.updateOrders(orders)
                binding.recyclerViewOrderHistory.visibility = View.VISIBLE
                binding.tvNoOrderHistory.visibility = View.GONE
            } else {
                binding.recyclerViewOrderHistory.visibility = View.GONE
                binding.tvNoOrderHistory.visibility = View.VISIBLE
            }
        } else {
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