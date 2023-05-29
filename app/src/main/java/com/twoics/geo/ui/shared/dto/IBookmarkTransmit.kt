package com.twoics.geo.ui.shared.dto

import com.twoics.geo.data.models.BookMark

interface IBookmarkTransmit {
    fun get(): BookMark?
    fun set(newBookmark: BookMark)
}