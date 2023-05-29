package com.twoics.geo.data.dao

import androidx.room.*
import com.twoics.geo.data.models.BookMark

@Dao
interface IBookmarksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmark(bookmark: BookMark)

    @Delete
    fun deleteBookmark(bookmark: BookMark)

    @Query("SELECT * FROM bookmark WHERE id = :id")
    fun getById(id: Int): BookMark?

    @Query("SELECT * FROM bookmark")
    fun getAll(): List<BookMark>
}
