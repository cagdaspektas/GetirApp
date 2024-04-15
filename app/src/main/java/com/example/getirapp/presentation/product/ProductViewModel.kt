package com.example.getirapp.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getirapp.common.domain.ViewState
import com.example.getirapp.domain.model.BaseResponse
import com.example.getirapp.domain.model.Product
import com.example.getirapp.domain.model.SuggestedProducts
import com.example.getirapp.domain.useCase.ProductUseCase
import com.example.getirapp.domain.useCase.SuggestedProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val  productUseCase: ProductUseCase,
    private val suggestedProductUseCase: SuggestedProductUseCase

) : ViewModel() {
    private val _uiStateProduct: MutableStateFlow<ViewState<BaseResponse.Success<List<Product>>>> =
        MutableStateFlow(ViewState.Loading)
    val uiStateProduct = _uiStateProduct.asStateFlow()

    private val _uiStateSuggestedProduct:MutableStateFlow<ViewState<BaseResponse.Success<List<SuggestedProducts>>>> =
            MutableStateFlow(ViewState.Loading)
    val uiStateSuggestedProduct = _uiStateSuggestedProduct.asStateFlow()

    //get product datas
    fun fetchProduct(){
        productUseCase.execute().map {
            when(val responseData:BaseResponse<List<Product>> = it){
                is BaseResponse.Success ->{
                    ViewState.Success(responseData)
                }
                is BaseResponse.Error ->{
                    ViewState.Error(responseData.message)
                }

        }
        }.onEach {
            _uiStateProduct.emit(it)
        }.catch {
            _uiStateProduct.emit(ViewState.Error(it.message.toString()))
        }.launchIn(viewModelScope)
    }

    //get suggested product datas
fun fetchSuggestedProducts(){
        suggestedProductUseCase.execute().map {
            when(val responseData:BaseResponse<List<SuggestedProducts>> = it){
                is BaseResponse.Success ->{
                    ViewState.Success(responseData)
                }
                is BaseResponse.Error ->{
                    ViewState.Error(responseData.message)
                }

            }
        }.onEach {
            _uiStateSuggestedProduct.emit(it)
        }.catch {
            _uiStateSuggestedProduct.emit(ViewState.Error(it.message.toString()))
        }.launchIn(viewModelScope)
}

}