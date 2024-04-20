package com.example.getirapp.presentation.basket

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.getirapp.R
import com.example.getirapp.common.domain.ViewState
import com.example.getirapp.databinding.FragmentBasketBinding
import com.example.getirapp.databinding.ItemProductCardBinding
import com.example.getirapp.domain.model.AddedProduct
import com.example.getirapp.domain.model.ProductItem
import com.example.getirapp.domain.model.SuggestedProductItem
import com.example.getirapp.presentation.adapter.SingleRecylerAdapter
import com.example.getirapp.presentation.product.ProductFragmentDirections
import com.wada811.viewbindingktx.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BasketFragment : Fragment(R.layout.fragment_basket) {
   private val binding by viewBinding(FragmentBasketBinding::bind)
    private val viewModel:BasketViewModel by viewModels()


    private val suggestedLinearAdapter = SingleRecylerAdapter<ItemProductCardBinding,SuggestedProductItem >(
        { inflater, _, _ ->
            ItemProductCardBinding.inflate(
                inflater,
                binding.rvSuggested,
                false
            )
        },
        { binding, item, position ->
            binding.apply {
                with(item) {
                    tvPrice.text = priceText.toString()
                    context?.let {
                        if (imageURL!=null) {
                            Glide.with(it)
                                .load(imageURL)
                                .into(imgProduct)


                        }
                        else {
                            Glide.with(it)
                                .load(squareThumbnailURL)
                                .into(imgProduct)

                        }
                    }
                    tvAttribute.text = shortDescription
                    tvName.text = name
                    itemProduct.setOnClickListener {

                        val action= ProductFragmentDirections.actionProductFragmentToProductFragmentDetail(
                            imageURL?:squareThumbnailURL!!,name!!, shortDescription?:"",priceText.toString()
                        )
                        findNavController().navigate(action)
                    }
                    btnAdd.setOnClickListener {
                        cardPiece.visibility=View.VISIBLE
                        btnDelete.visibility=View.VISIBLE
                       /* viewLifecycleOwner.lifecycleScope.launch {
                            dataStoreManager.productList.collect { product ->
                                product?.let { retrievedProduct ->
                                    println("Retrieved product: $retrievedProduct")
                                }
                            }
                        }*/


                    }



                }


            }


        }
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSuggestedProducts()
        with(binding){
            tlbarBasket.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

        }
    }

    private fun initSuggestedProducts() = with(viewModel) {
        viewModel.fetchSuggestedProducts()

        viewLifecycleOwner.lifecycleScope.launch {
            uiStateSuggestedProduct.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { viewState ->
                    when (viewState) {
                        is ViewState.Success -> {
                            val response = viewState.result

                            suggestedLinearAdapter.data = response.data[0].products!!

                            binding.rvSuggested.adapter = suggestedLinearAdapter

                            suggestedLinearAdapter.apply {

                            }

                        }

                        is ViewState.Error -> {
                            val responseError = viewState.error
                            Log.v("MyViewState", responseError)
                        }

                        is ViewState.Loading -> {
                            Log.v("MyViewState", "ViewState.Loading")
                        }

                    }

                }
        }
    }


}