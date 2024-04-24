package com.example.getirapp.util

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.getirapp.data.local.DataStoreManager
import com.example.getirapp.domain.model.AddedProduct
import com.example.getirapp.domain.model.ProductItem
import com.example.getirapp.domain.model.SuggestedProductItem
import com.example.getirapp.presentation.basket.BasketFragmentDirections
import com.example.getirapp.presentation.product.ProductFragmentDirections
import javax.inject.Inject

class GeneralFunctions @Inject constructor(
    private val dataStoreManager: DataStoreManager)
{
     suspend fun getPieceSuggested(item: SuggestedProductItem,  cardPiece: View? = null, btnDelete: View? = null, tvPiece: TextView) {
        dataStoreManager.productList.collect { product ->
            product.let { retrievedProduct ->
                val existingProductIndex = retrievedProduct.indexOfFirst {
                    it.suggestedItem?.id == item.id
                }

                if (existingProductIndex != -1) {
                    cardPiece?.visibility = View.VISIBLE
                    btnDelete?.visibility = View.VISIBLE
                    tvPiece.text = retrievedProduct[existingProductIndex].piece.toString()
                }
            }
        }
    }
    suspend fun getPieceProduct(item: ProductItem, cardPiece: View? = null, btnDelete: View? = null, tvPiece: TextView) {
        dataStoreManager.productList.collect { product ->
            product.let { retrievedProduct ->
                val existingProductIndex = retrievedProduct.indexOfFirst {
                    it.item?.id == item.id
                }

                if (existingProductIndex != -1) {
                    cardPiece?.visibility = View.VISIBLE
                    btnDelete?.visibility = View.VISIBLE
                    tvPiece.text = retrievedProduct[existingProductIndex].piece.toString()
                }
            }
        }
    }
    suspend fun addPieceSuggested(item: SuggestedProductItem,  tvPiece: TextView) {
        dataStoreManager.addSuggestedItem(AddedProduct(1, suggestedItem = item))

        dataStoreManager.productList.collect { product ->
            product.let { retrievedProduct ->
                val existingProductIndex = retrievedProduct.indexOfFirst {
                    it.suggestedItem?.id == item.id
                }

                if (existingProductIndex != -1) {
                    tvPiece.text = retrievedProduct[existingProductIndex].piece.toString()
                }
            }
        }
    }
    suspend fun addPieceProduct(item: ProductItem,  tvPiece: TextView) {
        dataStoreManager.addProductItem(AddedProduct(1, item = item))

        dataStoreManager.productList.collect { product ->
            product.let { retrievedProduct ->
                val existingProductIndex = retrievedProduct.indexOfFirst {
                    it.item?.id == item.id
                }

                if (existingProductIndex != -1) {
                    tvPiece.text = retrievedProduct[existingProductIndex].piece.toString()
                }
            }
        }
    }






}