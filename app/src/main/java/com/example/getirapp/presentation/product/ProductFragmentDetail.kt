package com.example.getirapp.presentation.product

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.getirapp.R
import com.example.getirapp.data.local.DataStoreManager
import com.example.getirapp.databinding.FragmentProductBinding
import com.example.getirapp.databinding.FragmentProductDetailBinding
import com.example.getirapp.domain.model.AddedProduct
import com.wada811.viewbindingktx.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ProductFragmentDetail : Fragment(R.layout.fragment_product_detail) {
    private val binding by viewBinding(FragmentProductDetailBinding::bind)
    private val args: ProductFragmentDetailArgs by navArgs()

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            lifecycleScope.launch {
                val initialTotalPrice = dataStoreManager.calculateTotalCost()
                totalBasket.text = String.format("₺%.2f", initialTotalPrice)

            }
            dataStoreManager.totalPriceLiveData.observe(viewLifecycleOwner) { totalPrice ->
                totalBasket.text = String.format("₺%.2f", totalPrice)

            }
            materialToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            btnBasketDetail.setOnClickListener {
                findNavController().navigate(R.id.action_productFragmentDetail_to_basketFragment)
            }



            context?.let {
                Glide.with(it)
                    .load(args.imgUrl)
                    .into(imgProduct)
            }
            tvName.text = args.productName
            tvPrice.text = args.price
            tvAttribute.text = args.attribute

            viewLifecycleOwner.lifecycleScope.launch {

                dataStoreManager.productList.collect { product ->
                    product.let { retrievedProduct ->

                        val existingProductIndex = retrievedProduct.indexOfFirst {
                            if (args.isSuggested) {
                                it.suggestedItem?.id == args.id

                            } else {
                                it.item?.id == args.id
                            }
                        }


                        if (existingProductIndex != -1) {
                            btnLinears.visibility = View.VISIBLE
                            btnAddBasketDetail.visibility = View.GONE

                            tvPiece.text = retrievedProduct[existingProductIndex].piece.toString()

                        }


                    }
                }


            }
            btnAddDetail.setOnClickListener {
                cardPiece.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE


                viewLifecycleOwner.lifecycleScope.launch {
                    dataStoreManager.addSuggestedItem(
                        AddedProduct(
                            1,
                            suggestedItem = args.suggestedItem
                        )
                    )

                    dataStoreManager.productList.collect { product ->
                        product.let { retrievedProduct ->
                            val existingProductIndex = retrievedProduct.indexOfFirst {
                                it.suggestedItem?.id == args.id
                            }

                            if (existingProductIndex != -1) {
                                tvPiece.text =
                                    retrievedProduct[existingProductIndex].piece.toString()
                            }
                        }
                    }


                }


            }

            btnDelete.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (tvPiece.text == "1") {
                        cardPiece.visibility = View.GONE
                        btnDelete.visibility = View.GONE
                        btnAddDetail.visibility = View.GONE
                        btnAddBasketDetail.visibility = View.VISIBLE
                    }
                    dataStoreManager.removeProductItem(AddedProduct(1, args.item))


                }
            }


            btnAddBasketDetail.setOnClickListener {
                btnLinears.visibility = View.VISIBLE
                btnAddBasketDetail.visibility = View.GONE

                viewLifecycleOwner.lifecycleScope.launch {
                    dataStoreManager.addSuggestedItem(
                        AddedProduct(
                            1,
                            suggestedItem = args.suggestedItem
                        )
                    )

                    dataStoreManager.productList.collect { product ->
                        product.let { retrievedProduct ->
                            val existingProductIndex = retrievedProduct.indexOfFirst {
                                it.suggestedItem?.id == args.id
                            }

                            if (existingProductIndex != -1) {
                                tvPiece.text =
                                    retrievedProduct[existingProductIndex].piece.toString()
                            }
                        }
                    }


                }


            }

        }

    }


}