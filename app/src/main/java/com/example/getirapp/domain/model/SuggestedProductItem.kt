package com.example.getirapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

data class SuggestedProductItem(
    val category: String?,
    val id: String?,
    val imageURL: String?,
    val name: String?,
    val price: Double?,
    val priceText: String?,
    val shortDescription: String?,
    val squareThumbnailURL: String?,
    val status: Int?,
    val unitPrice: Double?
) : Parcelable