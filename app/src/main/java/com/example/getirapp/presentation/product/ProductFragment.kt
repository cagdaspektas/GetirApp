package com.example.getirapp.presentation.product

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.getirapp.R
import com.example.getirapp.databinding.FragmentProductBinding
import com.wada811.viewbindingktx.viewBinding

class ProductFragment : Fragment(R.layout.fragment_product) {
    private val binding by viewBinding(FragmentProductBinding::bind)



    private val viewModel: ProductViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



}