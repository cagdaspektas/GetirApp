package com.example.getirapp.presentation.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.getirapp.R
import com.example.getirapp.common.domain.ViewState
import com.example.getirapp.data.local.DataStoreManager
import com.example.getirapp.databinding.FragmentProductBinding
import com.example.getirapp.databinding.ItemProductCardBinding
import com.example.getirapp.domain.model.AddedProduct
import com.example.getirapp.domain.model.ProductItem
import com.example.getirapp.domain.model.SuggestedProductItem
import com.example.getirapp.presentation.adapter.SingleRecylerAdapter
import com.wada811.viewbindingktx.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductFragment : Fragment(R.layout.fragment_product) {
    private val binding by viewBinding(FragmentProductBinding::bind)
    private val viewModel: ProductViewModel by viewModels()
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private val productLinearAdapter = SingleRecylerAdapter<ItemProductCardBinding,SuggestedProductItem >(
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
                    // sayfadaki adet alanları gözüksün diye
                    viewLifecycleOwner.lifecycleScope.launch {

                        dataStoreManager.productList.collect { product ->
                            product.let { retrievedProduct ->
                                val existingProductIndex = retrievedProduct.indexOfFirst {
                                    it.suggestedItem?.id == item.id
                                }


                                if (existingProductIndex != -1) {
                                    cardPiece.visibility=View.VISIBLE
                                    btnDelete.visibility=View.VISIBLE
                                    tvPiece.text = retrievedProduct[existingProductIndex].piece.toString()

                                }
                            }
                        }


                    }

                    itemProduct.setOnClickListener {

                        val action= ProductFragmentDirections.actionProductFragmentToProductFragmentDetail(
                            imageURL?:squareThumbnailURL!!,name!!, shortDescription?:"",
                            priceText.toString(),
                            true,
                            item.id!!,
                            item,
                            null,



                        )
                        findNavController().navigate(action)
                    }
                    btnAdd.setOnClickListener {
                        cardPiece.visibility=View.VISIBLE
                        btnDelete.visibility=View.VISIBLE

                        viewLifecycleOwner.lifecycleScope.launch {
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


                    }
                    btnDelete.setOnClickListener {
                        viewLifecycleOwner.lifecycleScope.launch {
                            if (tvPiece.text == "1") {
                                cardPiece.visibility=View.GONE
                                btnDelete.visibility=View.GONE
                            }
                            dataStoreManager.removeSuggestedItem(AddedProduct(1, suggestedItem = item))


                        }
                    }



                }


            }


        }
    )
    private val productGradientAdapter = SingleRecylerAdapter<ItemProductCardBinding, ProductItem?>(
        { inflater, _, _ ->
            ItemProductCardBinding.inflate(
                inflater,
                binding.rvProductsGradient,
                false
            )
        }
    ) { binding, item, position ->
        binding.apply {
            with(item!!) {
                tvPrice.text = priceText
                context?.let {
                    Glide.with(it)
                        .load(thumbnailURL)
                        .into(imgProduct)

                }

                tvAttribute.text = attribute
                tvName.text = name
                itemProduct.setOnClickListener {

                    val action =
                        ProductFragmentDirections.actionProductFragmentToProductFragmentDetail(
                            thumbnailURL!!, name!!, attribute ?: "", priceText.toString(),
                          false,
                            item.id!!,
                            null,
                            item,


                        )
                    findNavController().navigate(action)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                dataStoreManager.productList.collect { product ->
                    product.let { retrievedProduct ->
                        val existingProductIndex = retrievedProduct.indexOfFirst {
                            it.item?.id == item.id
                        }

                        if (existingProductIndex != -1) {
                            cardPiece.visibility = View.VISIBLE
                            btnDelete.visibility = View.VISIBLE
                            tvPiece.text = retrievedProduct[existingProductIndex].piece.toString()

                        }
                    }
                }


            }

            btnAdd.setOnClickListener {
                cardPiece.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE
                viewLifecycleOwner.lifecycleScope.launch {
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



            btnDelete.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (tvPiece.text == "1") {
                        cardPiece.visibility = View.GONE
                        btnDelete.visibility = View.GONE
                    }
                    dataStoreManager.removeProductItem(AddedProduct(1, item))


                }
            }

        }
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProduct()
        initSuggestedProducts()

        with(binding){

            lifecycleScope.launch {
                val initialTotalPrice = dataStoreManager.calculateTotalCost()
                totalBasket.text =String.format("₺%.2f", initialTotalPrice)

            }
            dataStoreManager.totalPriceLiveData.observe(viewLifecycleOwner) { totalPrice ->
                totalBasket.text =String.format("₺%.2f", totalPrice)
                //"₺${totalPrice}"
            }
            btnBasket.setOnClickListener {
                findNavController().navigate(R.id.action_productFragment_to_basketFragment)
            }
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
    private fun initSuggestedProducts() = with(viewModel) {
        viewModel.fetchSuggestedProducts()

        viewLifecycleOwner.lifecycleScope.launch {
            uiStateSuggestedProduct.flowWithLifecycle(viewLifecycleOwner.lifecycle)
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

}