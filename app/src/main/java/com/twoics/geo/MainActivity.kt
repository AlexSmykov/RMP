package com.twoics.geo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.twoics.geo.data.database.AppDatabase
import com.twoics.geo.data.repository.BookmarksRepository
import com.twoics.geo.data.repository.TestBookmarksRepository
import com.twoics.geo.map.Map
import com.twoics.geo.nav.Navigation
import com.twoics.geo.nav.Routes
import com.twoics.geo.ui.screens.search.SearchScreen
import com.twoics.geo.ui.screens.search.SearchViewModel
import com.twoics.geo.ui.shared.dto.TransmitBookmarkViewModel
import com.twoics.geo.ui.theme.GeoTheme
import org.osmdroid.util.GeoPoint

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "places.db"
        ).allowMainThreadQueries().build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applicationContext.deleteDatabase("places.db");

        super.onCreate(savedInstanceState)
        val repository = BookmarksRepository(db.bookmarkDao())

        val map = Map(
            defaultAreaRadius = 1000.0,
            defaultMapLocation = GeoPoint(56.0, 93.0)
        )

        setContent {
            GeoTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()

                    NavHost(navController, startDestination = Routes.SEARCH) {
                        composable(Routes.SEARCH) {
                            SearchScreen(
                                SearchViewModel(
                                    map = map,
                                    repository = repository,
                                )
                            ).Screen()
                        }
                    }
                }
            }
        }
    }
}
