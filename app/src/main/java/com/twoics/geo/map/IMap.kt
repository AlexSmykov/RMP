package com.twoics.geo.map

import androidx.compose.runtime.Composable
import com.twoics.geo.data.models.BookMark
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

interface IMap {
    val centerMapLocation: GeoPoint
    val areaRadius: Double

    fun clearPlaces()
    fun changeBookmarkActiveState()
    fun updateBookMarks(bookMarks: List<BookMark>)

    @Composable
    fun redrawMap(): MapView
}