package com.syrous.expensetracker.screen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syrous.expensetracker.data.local.model.Transaction
import com.syrous.expensetracker.databinding.LayoutTransactionViewHolderBinding

class TransactionAdapter: ListAdapter <Transaction, TransactionViewHolder>(callback){

    companion object {
        val callback = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
                oldItem.date == newItem.date && oldItem.amount == newItem.amount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutTransactionViewHolderBinding.inflate(layoutInflater)
        return TransactionViewHolder(binding, parent.context)
    }
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
       holder.bind(getItem(position), position)
    }
}

 class TransactionViewHolder(
        private val binding: LayoutTransactionViewHolderBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction, position: Int) {
            if (transaction.amount < 0)
                binding.amountTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.holo_red_dark
                    )
                )
            else
                binding.amountTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.holo_green_dark
                    )
                )

            binding.apply {
                amountTv.text = transaction.amount.toString()
                descriptionTv.text = transaction.description
                //TODO : Category id mapping to name
                date.text = transaction.date
                serialNumber.text = (position+1).toString()
            }
        }
    }
