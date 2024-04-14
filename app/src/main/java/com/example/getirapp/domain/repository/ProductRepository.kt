package com.example.getirapp.domain.repository

import com.example.getirapp.data.remote.ApiService
import com.example.getirapp.data.remote.CallBack
import com.example.getirapp.di.IoDispatcher
import com.example.getirapp.domain.model.BaseResponse
import com.example.getirapp.domain.model.Product
import com.example.getirapp.domain.model.SuggestedProducts
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
){

    fun fetchProduct(): Flow<BaseResponse<List<Product>>> = callbackFlow {
        apiService.fetchProducts().enqueue(CallBack(this.channel))
        awaitClose { close() }
    }.flowOn(ioDispatcher)

    fun fetchSuggestionProducts(): Flow<BaseResponse<List<SuggestedProducts>>> = callbackFlow {
        apiService.fetchSuggestedProducts().enqueue(CallBack(this.channel))
        awaitClose { close() }
    }.flowOn(ioDispatcher)



}