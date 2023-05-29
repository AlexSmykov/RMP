package com.twoics.geo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.twoics.geo.data.dao.IBookmarksDao
import com.twoics.geo.data.models.BookMark


@Database(entities = [BookMark::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): IBookmarksDao
}