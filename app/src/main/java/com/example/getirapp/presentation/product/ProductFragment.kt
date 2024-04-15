package com.example.getirapp.presentation.product

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.getirapp.R
import com.example.getirapp.common.domain.ViewState
import com.example.getirapp.databinding.FragmentProductBinding
import com.example.getirapp.databinding.ItemProductCardBinding
import com.example.getirapp.domain.model.BaseResponse
import com.example.getirapp.domain.model.Product
import com.example.getirapp.domain.model.ProductItem
import com.example.getirapp.presentation.adapter.SingleRecylerAdapter
import com.wada811.viewbindingktx.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : Fragment(R.layout.fragment_product) {
    private val binding by viewBinding(FragmentProductBinding::bind)
    private val viewModel: ProductViewModel by viewModels()


    private val productAdapter = SingleRecylerAdapter<ItemProductCardBinding, ProductItem>(
        { inflater, _, _ ->
            ItemProductCardBinding.inflate(
                inflater,
                binding.rvProducts,
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


            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        viewModel.fetchProduct()

        binding.btnBasket.setOnClickListener {
            findNavController().navigate(R.id.action_productFragment_to_productFragmentDetail)
        }
    }


    private fun initObserver() = with(viewModel) {

        // fetchSuggestedProducts()
        viewLifecycleOwner.lifecycleScope.launch {
            uiStateProduct.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { viewState ->
                    when (viewState) {
                        is ViewState.Success -> {
                            val response = viewState.result

                            productAdapter.data = response.data[0].products!!

                            binding.rvProducts.adapter = productAdapter

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