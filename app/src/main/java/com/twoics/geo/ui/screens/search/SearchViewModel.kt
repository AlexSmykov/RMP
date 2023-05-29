package com.twoics.geo.ui.screens.search

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoics.geo.data.models.BookMark
import com.twoics.geo.data.repository.IBookmarksRepository
import com.twoics.geo.map.IMap
import kotlinx.coroutines.launch
import okhttp3.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.concurrent.LinkedBlockingQueue

class SearchViewModel(
    private val map: IMap,
    private val repository: IBookmarksRepository,
) : ViewModel() {
    val mapView: MapView
        @Composable
        get() = map.redrawMap()


    fun getCurrentCords(): GeoPoint {
        return map.centerMapLocation
    }

    fun addBookmark(bookMark: BookMark) {
        viewModelScope.launch {
            repository.insertBookmark(bookMark)
        }

    }

    fun deleteBookMark(bookMark: BookMark) {
        viewModelScope.launch {
            repository.deleteBookmark(bookMark)
        }

    }

    fun getAll(): List<BookMark> {
        return repository.getAll()
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.changeAddingState -> {
                map.changeBookmarkActiveState()
            }

            is SearchEvent.updateBookMarks -> {
                map.updateBookMarks(getAll())
            }
        }
    }

    private val client = OkHttpClient()

    fun getPlace(latitude: Double, longitude: Double): String {
        val requestAddress = "https://nominatim.openstreetmap.org/reverse?addressdetails=0&accept-language=ru-RU&lat=" +
                latitude.toString() + "&lon=" + longitude.toString()
        val request = Request.Builder()
            .url(requestAddress)
            .build()

        val queue = LinkedBlockingQueue<String>()
        Thread {
            client.newCall(request).execute().use { response ->
                var tmp = ""
                tmp += response.body()?.string()
                tmp = tmp.split(">")[3]
                queue.add(tmp.split("<")[0])
            }
        }.start()
        return queue.take()
    }
}
