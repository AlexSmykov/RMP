package com.twoics.geo.ui.screens.search

sealed class SearchEvent {
    object updateBookMarks : SearchEvent()
    object changeAddingState : SearchEvent()
}
