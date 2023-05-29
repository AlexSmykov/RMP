package com.twoics.geo.nav

object Routes {
    const val SEARCH = "search"

    fun contains(route: String): Boolean {
        return route in listOf(SEARCH)
    }
}