package com.example.getirapp.domain.useCase

import com.example.getirapp.common.domain.NoParaMeterUseCase
import com.example.getirapp.domain.model.BaseResponse
import com.example.getirapp.domain.model.Product
import com.example.getirapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
): NoParaMeterUseCase<Flow<BaseResponse<List<Product>>>> {

    override fun execute(): Flow<BaseResponse<List<Product>>> = productRepository.fetchProduct()

}