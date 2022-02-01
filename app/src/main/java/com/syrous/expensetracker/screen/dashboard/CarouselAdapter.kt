package com.syrous.expensetracker.screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syrous.expensetracker.databinding.LayoutCategoryCardBinding
import com.syrous.expensetracker.model.DashboardCategoryItem


class CarouselAdapter : ListAdapter<DashboardCategoryItem, DashboardCategoryItemVH>(CALLBACK) {

    companion object {
        val CALLBACK = object : DiffUtil.ItemCallback<DashboardCategoryItem>() {
            override fun areContentsTheSame(
                oldItem: DashboardCategoryItem,
                newItem: DashboardCategoryItem
            ): Boolean = true

            override fun areItemsTheSame(
                oldItem: DashboardCategoryItem,
                newItem: DashboardCategoryItem
            ): Boolean = true

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardCategoryItemVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutCategoryCardBinding.inflate(layoutInflater, parent, false)
        return DashboardCategoryItemVH(binding)
    }

    override fun onBindViewHolder(holder: DashboardCategoryItemVH, position: Int) {
        holder.bind(getItem(position))
    }

}


class DashboardCategoryItemVH(private val binding: LayoutCategoryCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DashboardCategoryItem) {
        binding.categoryItemName.text = item.itemName
        binding.categoryItemAmount.text = "₹ ${item.amountSpent}"
    }

}


