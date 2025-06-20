package com.dicoding.projekakhirplatformkelompok5.ui.orderticket

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
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
    private var occupiedSeats = listOf<String>()

    private val selectedSeats = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ticketCount = it.getInt(ARG_TICKET_COUNT)
            occupiedSeats = it.getStringArrayList(ARG_OCCUPIED_SEATS) ?: listOf()

            Log.d("SeatDebug", "DIALOG DIBUAT - Menerima argumen: ticketCount=$ticketCount, occupiedSeats=$occupiedSeats")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogSeatPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSeatGrid()
        updateInfoText()

        binding.btnDoneSelectingSeats.setOnClickListener {
            setFragmentResult(
                REQUEST_KEY,
                bundleOf(RESULT_SEATS to ArrayList(selectedSeats))
            )
            dismiss()
        }
    }

    private fun setupSeatGrid() {
        val rows = 6
        val cols = 8

        binding.gridLayoutSeats.rowCount = rows
        binding.gridLayoutSeats.columnCount = cols + 1

        for (i in 0 until rows) {
            for (j in 0 until cols + 1) {
                if (j == 4) {
                    val space = View(requireContext())
                    val params = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(j, 1, 1f)
                    }
                    space.layoutParams = params
                    binding.gridLayoutSeats.addView(space)
                    continue
                }

                val seatName = "${('A' + i)}${if (j < 4) j + 1 else j}"

                val isOccupied = occupiedSeats.contains(seatName)

                if (seatName == "F4" || seatName == "F5" || seatName == "F7" || seatName == "F8") {
                    Log.d("SeatDebug", "DIALOG CHECK - Mengecek kursi '$seatName'. Apakah ada di list occupiedSeats? -> $isOccupied")
                }

                val seatView = TextView(requireContext()).apply {
                    text = seatName
                    width = 100
                    height = 100
                    gravity = android.view.Gravity.CENTER
                    textSize = 12f
                    layoutParams = GridLayout.LayoutParams().apply {
                        setMargins(8, 8, 8, 8)
                    }
                }

                when {
                    isOccupied -> {
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
            selectedSeats.remove(seatName)
            seatView.setBackgroundResource(R.drawable.seat_available)
        } else {
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
        private const val ARG_OCCUPIED_SEATS = "occupied_seats"

        fun newInstance(ticketCount: Int, occupiedSeats: ArrayList<String>): SeatPickerDialogFragment {
            return SeatPickerDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TICKET_COUNT, ticketCount)
                    putStringArrayList(ARG_OCCUPIED_SEATS, occupiedSeats)
                }
            }
        }
    }
}
