package com.twoics.geo.nav

import androidx.navigation.NavHostController

class Navigation(
    private val navHostController: NavHostController
) : INavigation {

    override fun navigate(route: String) {
        if (!Routes.contains(route)) {
            throw IllegalArgumentException("Route <$route> not exist")
        }

        navHostController.navigate(route)
    }
}