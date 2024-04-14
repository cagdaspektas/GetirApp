package com.example.getirapp.di

import com.example.getirapp.data.remote.ApiService
import com.example.getirapp.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideProductRepository(
        apiService: ApiService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ) = ProductRepository(apiService,ioDispatcher)





}