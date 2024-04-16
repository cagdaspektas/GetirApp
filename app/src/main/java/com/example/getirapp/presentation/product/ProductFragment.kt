package com.example.getirapp.presentation.product

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.getirapp.R
import com.example.getirapp.common.domain.ViewState
import com.example.getirapp.databinding.FragmentProductBinding
import com.example.getirapp.databinding.ItemProductCardBinding
import com.example.getirapp.domain.model.ProductItem
import com.example.getirapp.domain.model.SuggestedProductItem
import com.example.getirapp.presentation.adapter.SingleRecylerAdapter
import com.wada811.viewbindingktx.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : Fragment(R.layout.fragment_product) {
    private val binding by viewBinding(FragmentProductBinding::bind)
    private val viewModel: ProductViewModel by viewModels()


    private val productLinearAdapter = SingleRecylerAdapter<ItemProductCardBinding, ProductItem>(
        { inflater, _, _ ->
            ItemProductCardBinding.inflate(
                inflater,
                binding.rvProductsLinear,
                false
            )
        },
        { binding, item, position ->
            binding.apply {
                with(item) {
                    tvPrice.text = priceText.toString()
                    context?.let {
                        Glide.with(it)
                            .load(thumbnailURL)
                            .into(imgProduct)
                    }
                    tvAttribute.text = attribute
                    tvName.text = name
                }
                btnAdd.setOnClickListener {
                    cardPiece.visibility=View.VISIBLE
                    btnDelete.visibility=View.VISIBLE
                }

            }
        }
    )
    private val productGradientAdapter = SingleRecylerAdapter<ItemProductCardBinding, SuggestedProductItem?>(
        { inflater, _, _ ->
            ItemProductCardBinding.inflate(
                inflater,
                binding.rvProductsGradient,
                false
            )
        },
        { binding, item, position ->
            binding.apply {
                with(item!!) {
                    tvPrice.text = priceText.toString()
                    context?.let {
                    if (imageURL!=null) {
                        Glide.with(it)
                            .load(imageURL)
                        .into(imgProduct)}
                        else
                        Glide.with(it)
                            .load(squareThumbnailURL)
                            .into(imgProduct)
                    }
                    tvAttribute.text = shortDescription
                    tvName.text = name
                }
                btnAdd.setOnClickListener {
                    cardPiece.visibility=View.VISIBLE
                    btnDelete.visibility=View.VISIBLE
                }


            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProduct()
        initSuggestedProducts()



        binding.btnBasket.setOnClickListener {
            findNavController().navigate(R.id.action_productFragment_to_productFragmentDetail)
        }
    }


    private fun initProduct() = with(viewModel) {
        viewModel.fetchProduct()

        viewLifecycleOwner.lifecycleScope.launch {
            uiStateProduct.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { viewState ->
                    when (viewState) {
                        is ViewState.Success -> {
                            val response = viewState.result

                            productLinearAdapter.data = response.data[0].products!!

                            binding.rvProductsLinear.adapter = productLinearAdapter

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
    private fun initSuggestedProducts() = with(viewModel) {
        viewModel.fetchSuggestedProducts()
        viewLifecycleOwner.lifecycleScope.launch {
            uiStateSuggestedProduct.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { viewState ->
                    when (viewState) {
                        is ViewState.Success -> {
                            val response = viewState.result
                            productGradientAdapter.data = response.data[0].products!!
                            binding.rvProductsGradient.layoutManager=GridLayoutManager(context,3)
                            binding.rvProductsGradient.adapter = productGradientAdapter

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