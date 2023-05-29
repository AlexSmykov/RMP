package com.twoics.geo.data.repository

import com.twoics.geo.data.dao.IBookmarksDao
import com.twoics.geo.data.models.BookMark

class BookmarksRepository(
    private val dao: IBookmarksDao
) : IBookmarksRepository {
    override suspend fun insertBookmark(bookmark: BookMark) {
        dao.insertBookmark(bookmark)
    }

    override suspend fun deleteBookmark(bookmark: BookMark) {
        dao.deleteBookmark(bookmark)
    }

    override suspend fun getById(id: Int): BookMark? {
        return dao.getById(id)
    }

    override fun getAll(): List<BookMark> {
        return dao.getAll()
    }
}