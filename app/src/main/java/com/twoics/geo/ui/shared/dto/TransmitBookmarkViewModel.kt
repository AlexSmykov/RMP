package com.twoics.geo.ui.shared.dto

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.twoics.geo.data.models.BookMark

object TransmitBookmarkViewModel : ViewModel(), IBookmarkTransmit {
    private var bookmark by mutableStateOf<BookMark?>(null)

    override fun get(): BookMark? {
        return bookmark
    }

    override fun set(newBookmark: BookMark) {
        bookmark = newBookmark
    }
}