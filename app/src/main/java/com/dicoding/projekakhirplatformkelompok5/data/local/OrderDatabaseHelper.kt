package com.dicoding.projekakhirplatformkelompok5.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class OrderDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "CinemaZoneOrder.db"
        private const val TABLE_ORDERS = "orders"

        // Kolom Tabel Orders
        private const val KEY_ID = "id"
        private const val KEY_USER_ID = "user_id" // Akan diisi dengan UID dari Firebase Auth
        private const val KEY_MOVIE_TITLE = "movie_title"
        private const val KEY_LOCATION = "location"
        private const val KEY_SHOW_DATE = "show_date"
        private const val KEY_SHOW_TIME = "show_time"
        private const val KEY_STUDIO = "studio"
        private const val KEY_QUANTITY = "quantity"
        private const val KEY_SEAT = "seat"
        private const val KEY_PRICE = "price"
        private const val KEY_ORDER_DATE = "order_date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableOrders = ("CREATE TABLE $TABLE_ORDERS ("
                + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_USER_ID TEXT,"
                + "$KEY_MOVIE_TITLE TEXT,"
                + "$KEY_LOCATION TEXT,"
                + "$KEY_SHOW_DATE TEXT,"
                + "$KEY_SHOW_TIME TEXT,"
                + "$KEY_STUDIO TEXT,"
                + "$KEY_QUANTITY INTEGER,"
                + "$KEY_SEAT TEXT,"
                + "$KEY_PRICE INTEGER,"
                + "$KEY_ORDER_DATE TEXT" + ")")
        db?.execSQL(createTableOrders)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Cara sederhana untuk development, hapus tabel dan buat lagi.
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        onCreate(db)
    }

    fun addOrder(order: Order): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_USER_ID, order.userId)
            put(KEY_MOVIE_TITLE, order.movieTitle)
            put(KEY_LOCATION, order.location)
            put(KEY_SHOW_DATE, order.showDate)
            put(KEY_SHOW_TIME, order.showTime)
            put(KEY_STUDIO, order.studio)
            put(KEY_QUANTITY, order.quantity)
            put(KEY_SEAT, order.seat)
            put(KEY_PRICE, order.pricePerTicket)
            put(KEY_ORDER_DATE, order.orderDate)
        }
        val id = db.insert(TABLE_ORDERS, null, values)
        db.close()
        return id
    }

    fun getAllOrdersByUser(userId: String): List<Order> {
        val orderList = mutableListOf<Order>()
        val selectQuery = "SELECT * FROM $TABLE_ORDERS WHERE $KEY_USER_ID = ? ORDER BY $KEY_ID DESC"
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, arrayOf(userId))

        cursor?.use { c ->
            if (c.moveToFirst()) {
                do {
                    val order = Order(
                        id = c.getLong(c.getColumnIndexOrThrow(KEY_ID)),
                        userId = c.getString(c.getColumnIndexOrThrow(KEY_USER_ID)),
                        movieTitle = c.getString(c.getColumnIndexOrThrow(KEY_MOVIE_TITLE)),
                        quantity = c.getInt(c.getColumnIndexOrThrow(KEY_QUANTITY)),
                        seat = c.getString(c.getColumnIndexOrThrow(KEY_SEAT)),
                        orderDate = c.getString(c.getColumnIndexOrThrow(KEY_ORDER_DATE)),
                        location = c.getString(c.getColumnIndexOrThrow(KEY_LOCATION)),
                        showDate = c.getString(c.getColumnIndexOrThrow(KEY_SHOW_DATE)),
                        showTime = c.getString(c.getColumnIndexOrThrow(KEY_SHOW_TIME)),
                        studio = c.getString(c.getColumnIndexOrThrow(KEY_STUDIO)),
                        pricePerTicket = c.getInt(c.getColumnIndexOrThrow(KEY_PRICE))
                    )
                    orderList.add(order)
                } while (c.moveToNext())
            }
        }
        db.close()
        return orderList
    }
}