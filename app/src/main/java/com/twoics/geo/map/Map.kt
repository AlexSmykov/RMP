package com.twoics.geo.map

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.twoics.geo.R
import com.twoics.geo.data.models.BookMark
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


private object MapConstants {
    const val WEST_BORDER = -180.0
    const val SOUTH_BORDER = -85.0
    const val NORTH_BORDER = 85.0
    const val EAST_BORDER = 180.0
    const val MAX_ZOOM_LEVEL = 20.0
    const val MIN_ZOOM_LEVEL = 4.0
    const val START_ZOOM = 12.0
}

private data class MapMarker(
    val bookmark: BookMark,
    val marker: Marker
)

class Map(
    defaultAreaRadius: Double,
    defaultMapLocation: GeoPoint,
) : IMap {
    private lateinit var map: MapView
    private lateinit var addingBookmark: Marker

    private var bookmarkActive = true

    private var foundedBookMarks = ArrayList<MapMarker>()
    private var zoom: Double = MapConstants.START_ZOOM

    override var areaRadius: Double = defaultAreaRadius
    override var centerMapLocation: GeoPoint = defaultMapLocation
        private set

    private fun drawCurrentPlaces() {

        fun drawPlace(mapMarker: MapMarker) {
            val marker = mapMarker.marker
            val bookmark = mapMarker.bookmark

            marker.position = GeoPoint(bookmark.latitude, bookmark.longitude)
            map.overlays.add(marker);
        }

        foundedBookMarks.forEach {
            drawPlace(mapMarker = it)
        }

        map.invalidate()
    }

    override fun clearPlaces() {
        foundedBookMarks.forEach {
            map.overlays.remove(it.marker)
        }
        foundedBookMarks.clear()
        map.invalidate()
    }

    private fun addingBookmarkInit() {
        addingBookmark = Marker(map)
        bookmarkActive = false
        map.overlays.add(addingBookmark);
    }

    private fun drawAddingBookmark() {
        if (bookmarkActive) {
            addingBookmark.position = GeoPoint(
                centerMapLocation.latitude,
                centerMapLocation.longitude
            )
        }
        map.invalidate()
    }

    override fun changeBookmarkActiveState() {
        bookmarkActive = !bookmarkActive

        if (bookmarkActive) {
            addingBookmark.alpha = 1f
        }
        else {
            addingBookmark.alpha = 0f

        }

        drawAddingBookmark()

        map.invalidate()
    }

    @Composable
    override fun redrawMap(): MapView {
        this.map = generateMap()
        configureMap()
        return this.map
    }

    override fun updateBookMarks(bookMarks: List<BookMark>) {
        clearPlaces()

        bookMarks.forEach {
            foundedBookMarks.add(
                MapMarker(
                    bookmark = it,
                    marker = Marker(map)
                )
            )
        }

        drawCurrentPlaces()
    }

    private fun configureMap() {
        fun setScrollBorders() {
            map.setScrollableAreaLimitDouble(
                BoundingBox(
                    MapConstants.NORTH_BORDER,
                    MapConstants.EAST_BORDER,
                    MapConstants.SOUTH_BORDER,
                    MapConstants.WEST_BORDER
                )
            )
        }

        fun setScaleBorders() {
            map.maxZoomLevel = MapConstants.MAX_ZOOM_LEVEL
            map.minZoomLevel = MapConstants.MIN_ZOOM_LEVEL
            map.isHorizontalMapRepetitionEnabled = false
            map.isVerticalMapRepetitionEnabled = false
            map.setScrollableAreaLimitLatitude(
                MapView.getTileSystem().maxLatitude,
                MapView.getTileSystem().minLatitude, 0
            )
        }

        fun makeTouchable() {
            map.setMultiTouchControls(true)
        }

        fun setMapView() {
            map.controller.setZoom(zoom)
            map.controller.setCenter(centerMapLocation)
        }

        fun setMapListeners() {
            fun setMapCenter(point: IGeoPoint) {
                centerMapLocation = GeoPoint(point)
            }

            fun setCurrentZoom(zoom: Double) {
                this.zoom = zoom
            }

            this.map.setMapListener(DelayedMapListener(object : MapListener {
                override fun onScroll(paramScrollEvent: ScrollEvent): Boolean {
                    setMapCenter(map.mapCenter)
                    drawAddingBookmark()
                    return true
                }

                override fun onZoom(event: ZoomEvent): Boolean {
                    setCurrentZoom(map.zoomLevelDouble)
                    setMapCenter(map.mapCenter)
                    drawAddingBookmark()
                    return false
                }
            }))
        }

        setScrollBorders()
        setScaleBorders()
        makeTouchable()
        setMapView()
        setMapListeners()
        drawCurrentPlaces()
        addingBookmarkInit()
        drawAddingBookmark()
    }

    @Composable
    private fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
        remember(mapView) {
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    else -> {}
                }
            }
        }

    @Composable
    private fun generateMap(context: Context = LocalContext.current): MapView {
        val mapView = remember {
            MapView(context).apply {
                id = R.id.map
            }
        }

        val lifecycleObserver = rememberMapLifecycleObserver(mapView)
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifecycle) {
            lifecycle.addObserver(lifecycleObserver)
            onDispose {
                lifecycle.removeObserver(lifecycleObserver)
            }
        }

        Configuration.getInstance().userAgentValue = context.packageName

        return mapView
    }
}