package com.dicoding.projekakhirplatformkelompok5.ui.profile

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.projekakhirplatformkelompok5.R
import com.dicoding.projekakhirplatformkelompok5.data.local.Order
import com.dicoding.projekakhirplatformkelompok5.databinding.ActivityTicketDetailBinding
import com.google.gson.Gson
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.io.OutputStream

class TicketDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketDetailBinding
    private var order: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbarTicketDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Ambil data Order dari Intent
        order = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_ORDER, Order::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_ORDER)
        }

        order?.let {
            populateUi(it)
            generateQrCode(it)
        } ?: run {
            Toast.makeText(this, "Gagal memuat detail tiket", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.fabSaveTicket.setOnClickListener {
            val bitmap = getBitmapFromView(binding.ticketLayout)
            bitmap?.let { saveBitmapToGallery(this, it, "e-ticket-cinemazone-${order?.id}") }
        }
    }

    private fun populateUi(order: Order) {
        binding.tvTicketMovieTitle.text = order.movieTitle
        binding.tvTicketUser.text = "Pemesan: ${order.userId}"
        binding.tvTicketSeats.text = "Kursi: ${order.seat}"
        binding.tvTicketOrderId.text = "ID Pesanan: #${order.id}"
    }

    private fun generateQrCode(order: Order) {
        // 1. Buat string JSON dari data pesanan (tetap sama)
        val jsonString = Gson().toJson(order)

        // 2. Generate QR Code menggunakan ZXing
        val writer = MultiFormatWriter()
        try {
            // Encode string JSON menjadi BitMatrix (representasi 2D dari QR code)
            val bitMatrix = writer.encode(jsonString, BarcodeFormat.QR_CODE, 800, 800) // Ukuran 800x800 piksel

            // Konversi BitMatrix menjadi Bitmap
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    // Isi piksel bitmap dengan warna hitam atau putih
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            // Tampilkan Bitmap yang sudah jadi ke ImageView
            binding.ivQrCode.setImageBitmap(bitmap)

        } catch (e: WriterException) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal membuat QR Code", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk mengubah View menjadi Bitmap
    private fun getBitmapFromView(view: View): Bitmap? {
        return try {
            val bitmap = createBitmap(view.width, view.height)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Fungsi untuk menyimpan Bitmap ke galeri
    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, title: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$title.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CinemaZone")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        var uri: Uri? = null

        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                val outputStream: OutputStream? = resolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)
                }

                Toast.makeText(context, "Tiket berhasil disimpan ke galeri!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal menyimpan tiket.", Toast.LENGTH_SHORT).show()
            // Hapus entry jika terjadi error
            uri?.let { resolver.delete(it, null, null) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_ORDER = "extra_order"
    }
}