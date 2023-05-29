package com.twoics.geo.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class BookMark (
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    var latitude: Double,
    var longitude: Double,
    var description: String,
    var time: String,
) : Parcelable {
    override fun toString(): String =
                "id:" + id +
                " latitude:" + latitude +
                " longitude:" + longitude +
                " description:" + description +
                " time:" + time
}