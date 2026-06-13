package com.example.cinema

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "movies.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_MOVIES = "movies"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DIRECTOR = "director"
        const val COLUMN_YEAR = "year"
        const val COLUMN_GENRE = "genre"
        const val COLUMN_COST = "cost"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_MOVIES ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_TITLE TEXT, "
                + "$COLUMN_DIRECTOR TEXT, "
                + "$COLUMN_YEAR INTEGER, "
                + "$COLUMN_GENRE TEXT, "
                + "$COLUMN_COST REAL)")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES")
        onCreate(db)
    }

    fun addMovie(movie: Movie): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, movie.title)
        values.put(COLUMN_DIRECTOR, movie.director)
        values.put(COLUMN_YEAR, movie.year)
        values.put(COLUMN_GENRE, movie.genre)
        values.put(COLUMN_COST, movie.cost)
        
        val id = db.insert(TABLE_MOVIES, null, values)
        return id
    }

    fun getAllMovies(): List<Movie> {
        val movieList = mutableListOf<Movie>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_MOVIES, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val movie = Movie(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    director = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIRECTOR)),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YEAR)),
                    genre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)),
                    cost = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COST))
                )
                movieList.add(movie)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return movieList
    }
}
