package com.example.getirapp.domain.model

data class AddedProduct(
    var piece:Int?=1,
    val item:ProductItem?=null,
    val suggestedItem:SuggestedProductItem?=null
)
