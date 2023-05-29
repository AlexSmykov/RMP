package com.twoics.geo.ui.screens.search

import androidx.compose.ui.unit.Dp

class SearchScreenSizes(screenWidth: Dp) {
    private var _maxWidth: Dp

    private val _sheetMaxHeightShare: Float = 0.4f

    private val _sheetPeekHeightShare: Float = 0.15f
    private val _sheetShapeCornerShare: Float = 0.05f

    init {
        _maxWidth = screenWidth
    }

    val sheetMaxHeight: Float
        get() = _sheetMaxHeightShare

    val sheetPeakHeight: Dp
        get() = _maxWidth * _sheetPeekHeightShare

    val sheetCorner: Dp
        get() = _maxWidth * _sheetShapeCornerShare

}
