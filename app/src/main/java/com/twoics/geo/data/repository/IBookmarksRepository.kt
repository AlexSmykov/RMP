package com.twoics.geo.data.repository

import com.twoics.geo.data.models.BookMark

interface IBookmarksRepository {
    suspend fun insertBookmark(bookmark: BookMark)
    suspend fun deleteBookmark(bookmark: BookMark)
    suspend fun getById(id: Int): BookMark?
    fun getAll(): List<BookMark>
}