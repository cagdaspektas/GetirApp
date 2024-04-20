package com.example.getirapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.getirapp.domain.model.AddedProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        val PRODUCT_KEY = stringPreferencesKey("product_key")
    }

    private val gson = Gson()

    private val _totalPriceLiveData = MutableLiveData<Double>()
    val totalPriceLiveData: LiveData<Double> = _totalPriceLiveData




    private suspend fun saveProductList(products: List<AddedProduct>) {
        val productListJson = gson.toJson(products)
        dataStore.edit { preferences ->
            preferences[PRODUCT_KEY] = productListJson
        }
    }

    val productList: Flow<List<AddedProduct>> = dataStore.data.map { preferences ->
        val productListJson = preferences[PRODUCT_KEY]
        if (!productListJson.isNullOrEmpty()) {
            gson.fromJson(productListJson, object : TypeToken<List<AddedProduct>>() {}.type)
        } else {
            emptyList()
        }
    }

     suspend fun calculateTotalCost(): Double {
        var totalCost = 0.0
        productList.first().forEach { product ->
            product.piece?.let { piece ->
                if (product.item!=null) {
                    product.item.let { item ->
                        val price = item.price ?: 0.0
                        totalCost += price * piece
                    }
                }
                else{
                    product.suggestedItem.let { item ->
                        val price = item?.price ?: 0.0
                        totalCost += price * piece
                    }
                }
            }
        }
        _totalPriceLiveData.postValue(totalCost)
        return totalCost
    }




    suspend fun addProductItem(product: AddedProduct) {
        val currentList = productList.first().toMutableList()

        val existingProductIndex = currentList.indexOfFirst { it.item?.id == product.item?.id }
        if (existingProductIndex != -1) {
            currentList[existingProductIndex].piece = currentList[existingProductIndex].piece?.plus(1)
        } else {
            currentList.add(product)
        }

        saveProductList(currentList)
        calculateTotalCost()
    }
    suspend fun addSuggestedItem(product: AddedProduct) {
        val currentList = productList.first().toMutableList()

        val existingProductIndex = currentList.indexOfFirst { it.suggestedItem?.id == product.suggestedItem?.id }
        if (existingProductIndex != -1) {
            currentList[existingProductIndex].piece = currentList[existingProductIndex].piece?.plus(1)
        } else {
            currentList.add(product)
        }

        saveProductList(currentList)
        calculateTotalCost()
    }
    suspend fun removeProductItem(product: AddedProduct) {
        val currentList = productList.first().toMutableList()

        val existingProductIndex = currentList.indexOfFirst { it.item?.id == product.item?.id }
        if (existingProductIndex != -1) {
            val existingProduct = currentList[existingProductIndex]
            existingProduct.piece = existingProduct.piece?.minus(1)

            if (existingProduct.piece == 0) {
                currentList.removeAt(existingProductIndex)
            }
        }

        saveProductList(currentList)
        calculateTotalCost()
    }
    suspend fun removeSuggestedItem(product: AddedProduct) {
        val currentList = productList.first().toMutableList()
        val existingProductIndex = currentList.indexOfFirst { it.suggestedItem?.id == product.suggestedItem?.id }

        if (existingProductIndex != -1) {
            val existingProduct = currentList[existingProductIndex]
            existingProduct.piece = existingProduct.piece?.minus(1)

            if (existingProduct.piece == 0) {
                currentList.removeAt(existingProductIndex)
            }
            saveProductList(currentList)
            calculateTotalCost()
        } else {
            // Suggested item not found in the list, handle this case
            println("Suggested item not found in the list")
        }
    }


    suspend fun deleteProduct(product: AddedProduct) {
        val currentList = productList.first()
        val updatedList = currentList.toMutableList().apply { remove(product) }
        saveProductList(updatedList)
        calculateTotalCost()
    }
}

