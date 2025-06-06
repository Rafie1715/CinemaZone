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
        private const val KEY_USER_ID = "user_id"
        private const val KEY_MOVIE_TITLE = "movie_title"
        private const val KEY_QUANTITY = "quantity"
        private const val KEY_SEAT = "seat"
        private const val KEY_ORDER_DATE = "order_date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableOrders = ("CREATE TABLE $TABLE_ORDERS ("
                + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_USER_ID TEXT,"
                + "$KEY_MOVIE_TITLE TEXT,"
                + "$KEY_QUANTITY INTEGER,"
                + "$KEY_SEAT TEXT,"
                + "$KEY_ORDER_DATE TEXT" + ")")
        db?.execSQL(createTableOrders)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        onCreate(db)
    }

    fun addOrder(order: Order): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_USER_ID, order.userId)
            put(KEY_MOVIE_TITLE, order.movieTitle)
            put(KEY_QUANTITY, order.quantity)
            put(KEY_SEAT, order.seat)
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
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, arrayOf(userId))
            if (cursor.moveToFirst()) {
                do {
                    val order = Order(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                        userId = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_ID)),
                        movieTitle = cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOVIE_TITLE)),
                        quantity = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUANTITY)),
                        seat = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SEAT)),
                        orderDate = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ORDER_DATE))
                    )
                    orderList.add(order)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            // Handle exception
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }
        return orderList
    }
}