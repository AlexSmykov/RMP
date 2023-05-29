package com.twoics.geo.data.repository

import com.twoics.geo.data.models.BookMark
import java.util.*

class TestBookmarksRepository : IBookmarksRepository {
    private val bookmarks = arrayListOf<BookMark>()

    override suspend fun insertBookmark(bookmark: BookMark) {
        bookmarks.add(bookmark);
    }

    override suspend fun deleteBookmark(bookmark: BookMark) {
        val deleteMark: BookMark = bookmark.id?.let { get(it) } ?: return

        this.bookmarks.removeAll {
            it.id == deleteMark.id
        }
    }

    override suspend fun getById(id: Int): BookMark? {
        return get(id)
    }

    override fun getAll(): List<BookMark> {
        return this.bookmarks
    }

    private fun get(id: Int): BookMark? {
        return this.bookmarks.find {
            it.id == id
        }
    }
}