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
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "CinemaZoneWishlist.db"
        private const val TABLE_WISHLIST = "wishlist"

        private const val KEY_ROW_ID = "row_id"
        private const val KEY_MOVIE_ID = "movie_id"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_MOVIE_TITLE = "movie_title"
        private const val KEY_MOVIE_POSTER_PATH = "movie_poster_path"
        private const val KEY_MOVIE_OVERVIEW = "movie_overview"
        private const val KEY_MOVIE_RELEASE_DATE = "movie_release_date"
        private const val KEY_MOVIE_VOTE_AVERAGE = "movie_vote_average"
        private const val KEY_MOVIE_TRAILER_URL = "movie_trailer_url"
        private const val KEY_MOVIE_GENRES = "movie_genres"
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
                + "$KEY_MOVIE_TRAILER_URL TEXT,"
                + "$KEY_ADDED_DATE INTEGER,"
                + "$KEY_MOVIE_GENRES TEXT,"
                + "UNIQUE ($KEY_MOVIE_ID, $KEY_USER_ID) ON CONFLICT REPLACE"
                + ")")
        db?.execSQL(createTableWishlist)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
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
            put(KEY_MOVIE_GENRES, movie.genre?.joinToString(","))
            put(KEY_ADDED_DATE, System.currentTimeMillis())
        }
        val id = db.insertWithOnConflict(TABLE_WISHLIST, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return id
    }

    fun removeMovieFromWishlist(movieId: Int, userId: String): Int {
        val db = this.writableDatabase
        val count = db.delete(TABLE_WISHLIST, "$KEY_MOVIE_ID = ? AND $KEY_USER_ID = ?", arrayOf(movieId.toString(), userId))
        db.close()
        return count
    }

    fun isMovieInWishlist(movieId: Int, userId: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(TABLE_WISHLIST, arrayOf(KEY_MOVIE_ID), "$KEY_MOVIE_ID = ? AND $KEY_USER_ID = ?", arrayOf(movieId.toString(), userId), null, null, null)
        val exists = (cursor?.count ?: 0) > 0
        cursor?.close()
        db.close()
        return exists
    }

    fun getWishlistMoviesByUser(userId: String): List<Movie> {
        val movieList = mutableListOf<Movie>()
        val selectQuery = "SELECT * FROM $TABLE_WISHLIST WHERE $KEY_USER_ID = ? ORDER BY $KEY_ADDED_DATE DESC"
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, arrayOf(userId))

        cursor?.use { c ->
            if (c.moveToFirst()) {
                do {
                    val genresString = c.getString(c.getColumnIndexOrThrow(KEY_MOVIE_GENRES))
                    val genreList = genresString?.split(",") ?: emptyList()

                    val movie = Movie(
                        id = c.getInt(c.getColumnIndexOrThrow(KEY_MOVIE_ID)),
                        title = c.getString(c.getColumnIndexOrThrow(KEY_MOVIE_TITLE)),
                        overview = c.getString(c.getColumnIndexOrThrow(KEY_MOVIE_OVERVIEW)),
                        posterPath = c.getString(c.getColumnIndexOrThrow(KEY_MOVIE_POSTER_PATH)),
                        releaseDate = c.getString(c.getColumnIndexOrThrow(KEY_MOVIE_RELEASE_DATE)),
                        voteAverage = c.getDouble(c.getColumnIndexOrThrow(KEY_MOVIE_VOTE_AVERAGE)),
                        trailerUrl = c.getString(c.getColumnIndexOrThrow(KEY_MOVIE_TRAILER_URL)),
                        genre = genreList,
                        showtimes = null
                    )
                    movieList.add(movie)
                } while (c.moveToNext())
            }
        }
        db.close()
        return movieList
    }
}