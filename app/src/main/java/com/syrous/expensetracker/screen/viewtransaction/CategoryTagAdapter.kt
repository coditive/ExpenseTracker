package com.syrous.expensetracker.screen.viewtransaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syrous.expensetracker.databinding.LayoutCategoryTagViewHolderBinding


class CategoryTagAdapter : ListAdapter<String, CategoryTagVH>(CALLBACK) {

    companion object {
        val CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = true

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = true

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryTagVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutCategoryTagViewHolderBinding.inflate(layoutInflater)
        return CategoryTagVH(binding)
    }

    override fun onBindViewHolder(holder: CategoryTagVH, position: Int) {
        holder.bind(getItem(position))
    }
}


class CategoryTagVH(private val binding: LayoutCategoryTagViewHolderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(categoryTag: String) {
        binding.categoryTagName.text = categoryTag
    }
}