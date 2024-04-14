package com.example.getirapp.domain.model

sealed class BaseResponse<T> {

    data class Success<T>(val data: T): BaseResponse<T>()
    data class Error<T>(val message: String): BaseResponse<T>()


}
