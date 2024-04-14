package com.example.getirapp.di

import com.example.getirapp.domain.repository.ProductRepository
import com.example.getirapp.domain.useCase.ProductUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {


    @Provides
    @Singleton
    fun provideProductUseCase(productRepository: ProductRepository) =
        ProductUseCase(productRepository)



}