package com.luutuanhoang.assignment2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RentalItem(
    val name: String,
    var rating: Float,
    var multiChoiceAttribute: MutableList<String>,
    val pricePerMonth: Int,
    val description: String,
    val imageResId: Int
) : Parcelable
