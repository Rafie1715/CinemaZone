package com.dicoding.projekakhirplatformkelompok5.ui.orderticket

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderTicketFragment : Fragment() {

    private var _binding: FragmentOrderTicketBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    private var fullMovieList = listOf<Movie>()

    private var selectedMovie: Movie? = null
    private var selectedShowtime: Showtime? = null
    private var selectedShow: Show? = null
    private var selectedTimeSlot: TimeSlot? = null
    private var selectedSeats = listOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderTicketBinding.inflate(inflater, container, false)
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
                fetchBookedSeatsAndShowPicker(quantity)
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
                if (_binding != null) Toast.makeText(requireContext(), "Gagal memuat film: ${e.message}", Toast.LENGTH_SHORT).show()
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
        selectedMovie?.showtimes?.let { locations.addAll(it.map { s -> s.location }) }
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
        selectedShowtime?.shows?.let { dates.addAll(it.map { s -> s.date }) }
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

    private fun generateShowId(): String? {
        if (selectedMovie == null || selectedShowtime == null || selectedShow == null || selectedTimeSlot == null) {
            return null
        }
        val locationId = selectedShowtime!!.location.trim().replace(" ", "-").lowercase()
        val timeId = selectedTimeSlot!!.time.replace(":", "")
        val dateId = selectedShow!!.date
        val movieId = selectedMovie!!.id
        return "${movieId}_${locationId}_${dateId}_${timeId}"
    }

    private fun fetchBookedSeatsAndShowPicker(quantity: Int) {
        val showId = generateShowId()
        if (showId == null) {
            Toast.makeText(requireContext(), "Harap lengkapi pilihan film dan jadwal.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("SeatDebug", "================== FETCHING SEATS ==================")
        Log.d("SeatDebug", "Mencari data untuk Show ID: '$showId'")
        val showDocRef = db.collection("shows").document(showId)

        showDocRef.get().addOnSuccessListener { document ->
            val occupiedSeats = ArrayList<String>()
            if (document.exists()) {
                Log.d("SeatDebug", "Dokumen Show ID ditemukan!")
                val bookedSeatsMap = document.get("booked_seats") as? Map<String, Any>
                if (bookedSeatsMap != null) {
                    occupiedSeats.addAll(bookedSeatsMap.keys)
                    Log.d("SeatDebug", "Kursi terisi yang ditemukan: $occupiedSeats")
                } else {
                    Log.d("SeatDebug", "Field 'booked_seats' KOSONG atau BUKAN MAP.")
                }
            } else {
                Log.d("SeatDebug", "Dokumen untuk Show ID ini TIDAK ditemukan di Firestore.")
            }

            Log.d("SeatDebug", "Mengirim ${occupiedSeats.size} kursi terisi ke dialog.")
            SeatPickerDialogFragment.newInstance(quantity, occupiedSeats).show(parentFragmentManager, SeatPickerDialogFragment.TAG)
        }.addOnFailureListener { e ->
            Log.e("SeatDebug", "FETCH GAGAL TOTAL: ", e)
        }
    }

    private fun processOrder() {
        val user = auth.currentUser
        val quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 0

        if (user == null || selectedMovie == null || selectedShowtime == null || selectedShow == null || selectedTimeSlot == null || quantity <= 0 || selectedSeats.size != quantity) {
            Toast.makeText(requireContext(), "Harap lengkapi semua pilihan dengan benar.", Toast.LENGTH_LONG).show()
            return
        }

        val showId = generateShowId()!!
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

        val newBookedSeats = hashMapOf<String, Boolean>()
        selectedSeats.forEach { seat ->
            newBookedSeats[seat] = true
        }

        val showUpdates = mapOf("booked_seats" to newBookedSeats)


        Log.d("SeatDebug", "SAVE - Menyimpan pesanan untuk Show ID: '$showId'")
        Log.d("SeatDebug", "SAVE - Data kursi yang akan diupdate: $showUpdates")

        val showDocRef = db.collection("shows").document(showId)
        val userOrderRef = db.collection("users").document(user.uid).collection("orders").document()

        db.runBatch { batch ->
            batch.set(userOrderRef, order)
            batch.set(showDocRef, showUpdates, SetOptions.merge())
        }
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Tiket berhasil dipesan!", Toast.LENGTH_LONG).show()
                binding.spinnerMovie.setText("", false)
                resetAllViews()
                loadOrderHistory()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal memesan tiket: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("OrderTicketFragment", "Error writing batch", e)
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
        if (user == null) {
            // Cek binding sebelum update UI
            if (_binding != null) {
                orderHistoryAdapter.updateOrders(emptyList())
                binding.recyclerViewOrderHistory.visibility = View.GONE
                binding.tvNoOrderHistory.visibility = View.VISIBLE
                binding.tvNoOrderHistory.text = "Login untuk melihat riwayat pesanan."
            }
            return
        }

        val ordersRef = db.collection("users").document(user.uid).collection("orders")
            .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(5)

        ordersRef.get()
            .addOnSuccessListener { result ->
                if (_binding == null) return@addOnSuccessListener

                val orderList = result.map { document ->
                    document.toObject(Order::class.java).apply { id = document.id }
                }

                if (orderList.isNotEmpty()) {
                    orderHistoryAdapter.updateOrders(orderList)
                    binding.recyclerViewOrderHistory.visibility = View.VISIBLE
                    binding.tvNoOrderHistory.visibility = View.GONE
                } else {
                    binding.recyclerViewOrderHistory.visibility = View.GONE
                    binding.tvNoOrderHistory.visibility = View.VISIBLE
                    binding.tvNoOrderHistory.text = "Belum ada riwayat pesanan."
                }
            }
            .addOnFailureListener { exception ->
                if (_binding == null) return@addOnFailureListener

                Log.e("OrderTicketFragment", "Error getting order history", exception)
                binding.recyclerViewOrderHistory.visibility = View.GONE
                binding.tvNoOrderHistory.visibility = View.VISIBLE
                binding.tvNoOrderHistory.text = "Gagal memuat riwayat pesanan."
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}