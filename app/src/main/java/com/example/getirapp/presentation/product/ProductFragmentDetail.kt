package com.example.getirapp.presentation.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.getirapp.R
import com.example.getirapp.databinding.FragmentProductBinding
import com.example.getirapp.databinding.FragmentProductDetailBinding
import com.wada811.viewbindingktx.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp


@AndroidEntryPoint
class ProductFragmentDetail : Fragment(R.layout.fragment_product_detail) {
    private val binding by viewBinding(FragmentProductDetailBinding::bind)
    private val viewModel: ProductViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
with(binding){
    materialToolbar.setNavigationOnClickListener {
        findNavController().popBackStack()
    }

  btnAddBasketDetail.setOnClickListener {
    btnLinears.visibility=View.VISIBLE
      btnAddBasketDetail.visibility=View.GONE
      btnBasketDetail.visibility=View.VISIBLE
    }

}

    }


}