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
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.projekakhirplatformkelompok5.data.local.Order
import com.dicoding.projekakhirplatformkelompok5.databinding.ActivityTicketDetailBinding
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.io.OutputStream
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

class TicketDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketDetailBinding
    private var order: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarTicketDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
            val bitmapToSave = getBitmapFromView(binding.ticketLayout)
            if (bitmapToSave != null) {
                saveBitmapToGallery(this, bitmapToSave, "e-ticket-cinemazone-${order?.id}")
            } else {
                Toast.makeText(this, "Gagal membuat gambar tiket", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateUi(order: Order) {
        binding.tvTicketMovieTitle.text = order.movieTitle
        binding.tvTicketLocation.text = order.location
        binding.tvTicketUser.text = order.userId
        binding.tvTicketShowTime.text = "${order.showDate}, ${order.showTime} - ${order.studio}"
        binding.tvTicketSeats.text = "Kursi: ${order.seat} (${order.quantity} Tiket)"
        binding.tvTicketOrderId.text = "ID Pesanan: #${order.id}"
    }

    private fun generateQrCode(order: Order) {
        val jsonString = Gson().toJson(order)
        val writer = MultiFormatWriter()
        try {
            val bitMatrix = writer.encode(jsonString, BarcodeFormat.QR_CODE, 800, 800)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }
            binding.ivQrCode.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal membuat QR Code", Toast.LENGTH_SHORT).show()
        }
    }

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
            uri?.let { resolver.delete(it, null, null) }
            Toast.makeText(context, "Gagal menyimpan tiket.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
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