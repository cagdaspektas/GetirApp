package com.example.getirapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.getirapp.domain.model.ProductItem

class SingleRecylerAdapter<B: ViewBinding,DATA>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> B,
    private val bind: (B,DATA, Int?) -> Unit,
): RecyclerView.Adapter<SingleRecylerAdapter<B,DATA>.BindingViewHolder<B>>()
{

    var data = listOf<DATA>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<B> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = bindingInflater(layoutInflater,parent,false)
        return  BindingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BindingViewHolder<B>, position: Int) {
        val item = data[position]
        bind(holder.binding,item, position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    inner class BindingViewHolder<B: ViewBinding>(val binding: B): RecyclerView.ViewHolder(binding.root)

}