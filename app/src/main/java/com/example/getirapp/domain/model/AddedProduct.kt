package com.example.getirapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize

data class AddedProduct(
    var piece:Int?=1,
    val item:ProductItem?=null,
    val suggestedItem:SuggestedProductItem?=null
) : Parcelable
