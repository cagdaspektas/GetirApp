package com.example.getirapp.data.remote

import com.example.getirapp.domain.model.Product
import com.example.getirapp.domain.model.SuggestedProducts
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("products")
    fun fetchProducts():Call<List<Product>>

    @GET("suggestedProducts")
    fun fetchSuggestedProducts(): Call<List<SuggestedProducts>>
}