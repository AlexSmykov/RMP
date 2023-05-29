package com.twoics.geo.ui.shared.event

sealed class UiEvent {
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ): UiEvent()
}