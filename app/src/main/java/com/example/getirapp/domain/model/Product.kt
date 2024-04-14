package com.example.getirapp.domain.model

data class Product(
    val email: String?,
    val id: String?,
    val name: String?,
    val password: String?,
    val productCount: Int?,
    val products: List<ProductItem>?
)