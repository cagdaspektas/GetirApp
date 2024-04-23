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
import com.example.getirapp.data.local.DataStoreManager
import com.example.getirapp.databinding.FragmentBasketBinding
import com.example.getirapp.databinding.ItemBasketCardBinding
import com.example.getirapp.databinding.ItemProductCardBinding
import com.example.getirapp.domain.model.AddedProduct
import com.example.getirapp.domain.model.ProductItem
import com.example.getirapp.domain.model.SuggestedProductItem
import com.example.getirapp.presentation.adapter.SingleRecylerAdapter
import com.example.getirapp.presentation.product.ProductFragmentDirections
import com.wada811.viewbindingktx.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BasketFragment : Fragment(R.layout.fragment_basket) {
    private val binding by viewBinding(FragmentBasketBinding::bind)
    private val viewModel: BasketViewModel by viewModels()
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private val suggestedLinearAdapter =
        SingleRecylerAdapter<ItemProductCardBinding, SuggestedProductItem>(
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
    private val basketAdapter = SingleRecylerAdapter<ItemBasketCardBinding, AddedProduct>(
        { inflater, _, _ ->
            ItemBasketCardBinding.inflate(
                inflater,
                binding.rvBasket,
                false
            )
        },
        { binding, products, position ->
            binding.apply {
                with(products) {
                    if (item != null) {
                        with(item) {
                            tvPriceBasket.text = priceText.toString()
                            context?.let {
                                Glide.with(it)
                                    .load(thumbnailURL)
                                    .into(imgProductBasket)

                            }
                            tvAttributeBasket.text = attribute
                            tvNameBasket.text = name
                            viewLifecycleOwner.lifecycleScope.launch {
                                dataStoreManager.productList.collect { product ->
                                    product.let { retrievedProduct ->
                                        val existingProductIndex = retrievedProduct.indexOfFirst {
                                            it.item?.id == item.id
                                        }

                                        if (existingProductIndex != -1) {

                                            tvPieceBasket.text =
                                                retrievedProduct[existingProductIndex].piece.toString()

                                        }
                                    }
                                }


                            }

                            btnAddBasket.setOnClickListener {

                                viewLifecycleOwner.lifecycleScope.launch {
                                    dataStoreManager.addProductItem(AddedProduct(1, item = item))
                                    dataStoreManager.productList.collect { product ->
                                        product.let { retrievedProduct ->
                                            val existingProductIndex = retrievedProduct.indexOfFirst {
                                                it.item?.id == item.id
                                            }

                                            if (existingProductIndex != -1) {
                                                tvPieceBasket.text = retrievedProduct[existingProductIndex].piece.toString()
                                            }
                                        }
                                    }


                                }
                            }



                            btnDeleteBasket.setOnClickListener {
                                viewLifecycleOwner.lifecycleScope.launch {

                                    dataStoreManager.removeProductItem(AddedProduct(1, item))


                                }
                            }
                        }
                    }
                    else{
                        with(suggestedItem!!){
                            tvPriceBasket.text = priceText.toString()
                            context?.let {
                                if (imageURL!=null) {
                                    Glide.with(it)
                                        .load(imageURL)
                                        .into(imgProductBasket)


                                }
                                else {
                                    Glide.with(it)
                                        .load(squareThumbnailURL)
                                        .into(imgProductBasket)

                                }
                            }
                            tvAttributeBasket.text = shortDescription
                            tvNameBasket.text = name
                            viewLifecycleOwner.lifecycleScope.launch {
                                dataStoreManager.productList.collect { product ->
                                    product.let { retrievedProduct ->
                                        val existingProductIndex = retrievedProduct.indexOfFirst {
                                            it.suggestedItem?.id == suggestedItem.id
                                        }

                                        if (existingProductIndex != -1) {

                                            tvPieceBasket.text =
                                                retrievedProduct[existingProductIndex].piece.toString()

                                        }
                                    }
                                }


                            }
                            btnAddBasket.setOnClickListener {

                                viewLifecycleOwner.lifecycleScope.launch {
                                    dataStoreManager.addSuggestedItem(AddedProduct(1, suggestedItem = item))

                                    dataStoreManager.productList.collect { product ->
                                        product.let { retrievedProduct ->
                                            val existingProductIndex = retrievedProduct.indexOfFirst {
                                                it.suggestedItem?.id == suggestedItem.id
                                            }

                                            if (existingProductIndex != -1) {
                                                tvPieceBasket.text = retrievedProduct[existingProductIndex].piece.toString()
                                            }
                                        }
                                    }


                                }


                            }
                            btnDeleteBasket.setOnClickListener {
                                viewLifecycleOwner.lifecycleScope.launch {

                                    dataStoreManager.removeSuggestedItem(AddedProduct(1, suggestedItem = suggestedItem))


                                }
                            }
                        }

                    }

                }

            }


        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSuggestedProducts()
        with(binding) {
            tlbarBasket.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            viewLifecycleOwner.lifecycleScope.launch {
                dataStoreManager.productList.collect { product ->
                    product.let { retrievedProduct ->

                        basketAdapter.data= retrievedProduct
                        rvBasket.adapter = basketAdapter


                    }
                }


            }
            lifecycleScope.launch {
                val initialTotalPrice = dataStoreManager.calculateTotalCost()
                totalPriceBasket.text =String.format("₺%.2f", initialTotalPrice)

            }
            dataStoreManager.totalPriceLiveData.observe(viewLifecycleOwner) { totalPrice ->
                totalPriceBasket.text =String.format("₺%.2f", totalPrice)
            }
            icRubbish.setOnClickListener {
                deleteAllProduct()
            }

            btnAddBasketDetail.setOnClickListener {
                deleteAllProduct()
            }

        }

    }
private fun deleteAllProduct(){
    viewLifecycleOwner.lifecycleScope.launch {
        dataStoreManager.deleteAllProducts()
        initSuggestedProducts()

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