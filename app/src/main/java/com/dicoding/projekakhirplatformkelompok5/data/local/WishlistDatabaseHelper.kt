package com.dicoding.projekakhirplatformkelompok5.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dicoding.projekakhirplatformkelompok5.data.model.Movie

class WishlistDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // NAIKKAN VERSI DATABASE KARENA ADA PERUBAHAN SKEMA
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "CinemaZoneWishlist.db"
        private const val TABLE_WISHLIST = "wishlist"

        // Kolom Tabel Wishlist
        private const val KEY_ROW_ID = "row_id"
        private const val KEY_MOVIE_ID = "movie_id"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_MOVIE_TITLE = "movie_title"
        private const val KEY_MOVIE_POSTER_PATH = "movie_poster_path"
        private const val KEY_MOVIE_OVERVIEW = "movie_overview"
        private const val KEY_MOVIE_RELEASE_DATE = "movie_release_date"
        private const val KEY_MOVIE_VOTE_AVERAGE = "movie_vote_average"
        // TAMBAHKAN KEY BARU UNTUK TRAILER URL
        private const val KEY_MOVIE_TRAILER_URL = "movie_trailer_url"
        private const val KEY_ADDED_DATE = "added_date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableWishlist = ("CREATE TABLE $TABLE_WISHLIST ("
                + "$KEY_ROW_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_MOVIE_ID INTEGER,"
                + "$KEY_USER_ID TEXT NOT NULL,"
                + "$KEY_MOVIE_TITLE TEXT,"
                + "$KEY_MOVIE_POSTER_PATH TEXT,"
                + "$KEY_MOVIE_OVERVIEW TEXT,"
                + "$KEY_MOVIE_RELEASE_DATE TEXT,"
                + "$KEY_MOVIE_VOTE_AVERAGE REAL,"
                // TAMBAHKAN KOLOM BARU DI SINI
                + "$KEY_MOVIE_TRAILER_URL TEXT,"
                + "$KEY_ADDED_DATE INTEGER,"
                + "UNIQUE ($KEY_MOVIE_ID, $KEY_USER_ID) ON CONFLICT REPLACE"
                + ")")
        db?.execSQL(createTableWishlist)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Hapus tabel lama dan buat lagi (cara sederhana untuk development)
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_WISHLIST")
        onCreate(db)
    }

    fun addMovieToWishlist(movie: Movie, userId: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_MOVIE_ID, movie.id)
            put(KEY_USER_ID, userId)
            put(KEY_MOVIE_TITLE, movie.title)
            put(KEY_MOVIE_POSTER_PATH, movie.posterPath)
            put(KEY_MOVIE_OVERVIEW, movie.overview)
            put(KEY_MOVIE_RELEASE_DATE, movie.releaseDate)
            put(KEY_MOVIE_VOTE_AVERAGE, movie.voteAverage)
            put(KEY_MOVIE_TRAILER_URL, movie.trailerUrl)
            put(KEY_ADDED_DATE, System.currentTimeMillis())
        }
        val id = db.insertWithOnConflict(TABLE_WISHLIST, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return id
    }

    fun removeMovieFromWishlist(movieId: Int, userId: String): Int {
        val db = this.writableDatabase
        // Hapus berdasarkan movieId DAN userId
        val count = db.delete(TABLE_WISHLIST, "$KEY_MOVIE_ID = ? AND $KEY_USER_ID = ?", arrayOf(movieId.toString(), userId))
        db.close()
        return count // Mengembalikan jumlah baris yang dihapus
    }

    fun isMovieInWishlist(movieId: Int, userId: String): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        var isInWishlist = false
        try {
            cursor = db.query(
                TABLE_WISHLIST,
                arrayOf(KEY_MOVIE_ID),
                "$KEY_MOVIE_ID = ? AND $KEY_USER_ID = ?",
                arrayOf(movieId.toString(), userId),
                null, null, null
            )
            isInWishlist = cursor.count > 0
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }
        return isInWishlist
    }

    fun getWishlistMoviesByUser(userId: String): List<Movie> {
        val movieList = mutableListOf<Movie>()
        val selectQuery = "SELECT * FROM $TABLE_WISHLIST WHERE $KEY_USER_ID = ? ORDER BY $KEY_ADDED_DATE DESC"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, arrayOf(userId))
            if (cursor.moveToFirst()) {
                do {
                    // ===== PERBAIKI PEMBUATAN OBJEK MOVIE DI SINI =====
                    val movie = Movie(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MOVIE_ID)),
                        title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOVIE_TITLE)),
                        overview = cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOVIE_OVERVIEW)),
                        posterPath = cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOVIE_POSTER_PATH)),
                        releaseDate = cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOVIE_RELEASE_DATE)),
                        voteAverage = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_MOVIE_VOTE_AVERAGE)),
                        trailerUrl = cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOVIE_TRAILER_URL))
                    )
                    // ====================================================
                    movieList.add(movie)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }
        return movieList
    }
}