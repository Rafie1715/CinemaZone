package com.dicoding.projekakhirplatformkelompok5.ui.orderticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.GridLayout.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.dicoding.projekakhirplatformkelompok5.R
import com.dicoding.projekakhirplatformkelompok5.databinding.DialogSeatPickerBinding

class SeatPickerDialogFragment : DialogFragment() {

    private var _binding: DialogSeatPickerBinding? = null
    private val binding get() = _binding!!

    private var ticketCount = 0
    private val selectedSeats = mutableListOf<String>()

    // Anggap beberapa kursi sudah terisi
    private val occupiedSeats = listOf("A3", "A4", "C5", "D1", "D2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ambil jumlah tiket dari argumen
        ticketCount = arguments?.getInt(ARG_TICKET_COUNT) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSeatPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        setupSeatGrid()
        updateInfoText()

        binding.btnDoneSelectingSeats.setOnClickListener {
            // Kirim hasil kembali ke OrderTicketFragment
            setFragmentResult(
                REQUEST_KEY,
                bundleOf(RESULT_SEATS to ArrayList(selectedSeats)) // Kirim sebagai ArrayList
            )
            dismiss() // Tutup dialog
        }
    }

    private fun setupSeatGrid() {
        val rows = 6
        val cols = 8

        binding.gridLayoutSeats.rowCount = rows
        binding.gridLayoutSeats.columnCount = cols + 1 // +1 untuk space di tengah

        for (i in 0 until rows) {
            for (j in 0 until cols + 1) { // Looping sampai cols + 1
                // Beri jarak di tengah untuk jalan pada kolom ke-4 (indeks 4)
                if (j == 4) {
                    val space = View(context)
                    // Gunakan GridLayout.LayoutParams
                    val params = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        // Koreksi ada di sini: Gunakan GridLayout.spec
                        columnSpec = GridLayout.spec(j, 1, 1f) // Buat space fleksibel
                    }
                    space.layoutParams = params
                    binding.gridLayoutSeats.addView(space)
                    continue
                }

                val seatName = "${(i + 'A'.code).toChar()}${if (j < 4) j + 1 else j}"
                val seatView = TextView(context).apply {
                    text = seatName
                    width = 100
                    height = 100
                    gravity = android.view.Gravity.CENTER
                    textSize = 12f
                    // Gunakan GridLayout.LayoutParams
                    layoutParams = GridLayout.LayoutParams().apply {
                        setMargins(8, 8, 8, 8)
                    }
                }

                when {
                    occupiedSeats.contains(seatName) -> {
                        seatView.setBackgroundResource(R.drawable.seat_occupied)
                        seatView.isEnabled = false
                    }
                    else -> {
                        seatView.setBackgroundResource(R.drawable.seat_available)
                        seatView.setOnClickListener { onSeatClicked(it as TextView, seatName) }
                    }
                }
                binding.gridLayoutSeats.addView(seatView)
            }
        }
    }

    private fun onSeatClicked(seatView: TextView, seatName: String) {
        if (selectedSeats.contains(seatName)) {
            // Batal memilih kursi
            selectedSeats.remove(seatName)
            seatView.setBackgroundResource(R.drawable.seat_available)
        } else {
            // Memilih kursi baru
            if (selectedSeats.size < ticketCount) {
                selectedSeats.add(seatName)
                seatView.setBackgroundResource(R.drawable.seat_selected)
            } else {
                Toast.makeText(context, "Anda hanya bisa memilih $ticketCount kursi.", Toast.LENGTH_SHORT).show()
            }
        }
        updateInfoText()
    }

    private fun updateInfoText() {
        val remaining = ticketCount - selectedSeats.size
        if (remaining > 0) {
            binding.tvInfoPemilihanKursi.text = "Pilih $remaining kursi lagi."
            binding.btnDoneSelectingSeats.isEnabled = false
        } else {
            binding.tvInfoPemilihanKursi.text = "Anda telah memilih $ticketCount kursi."
            binding.btnDoneSelectingSeats.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SeatPickerDialog"
        const val REQUEST_KEY = "seat_picker_request"
        const val RESULT_SEATS = "selected_seats"
        const val ARG_TICKET_COUNT = "ticket_count"

        fun newInstance(ticketCount: Int): SeatPickerDialogFragment {
            val args = Bundle().apply {
                putInt(ARG_TICKET_COUNT, ticketCount)
            }
            return SeatPickerDialogFragment().apply {
                arguments = args
            }
        }
    }
}
