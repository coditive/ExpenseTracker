package com.syrous.expensetracker.screen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syrous.expensetracker.data.local.model.TransactionCategory
import com.syrous.expensetracker.data.local.model.UserTransaction
import com.syrous.expensetracker.databinding.LayoutTransactionViewHolderBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter : ListAdapter<UserTransaction, TransactionViewHolder>(callback) {

    companion object {
        val callback = object : DiffUtil.ItemCallback<UserTransaction>() {
            override fun areItemsTheSame(
                oldItem: UserTransaction,
                newItem: UserTransaction
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: UserTransaction,
                newItem: UserTransaction
            ): Boolean =
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

    fun bind(transaction: UserTransaction, position: Int) {
        binding.apply {
            serialNumber.text = transaction.id.toString()
            if (transaction.transactionCategory == TransactionCategory.EXPENSE)
                amountTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.holo_red_dark
                    )
                )
            else
                amountTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.holo_green_dark
                    )
                )
            descriptionTv.text = transaction.description
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            date.text = dateFormatter.format(transaction.date)
            amountTv.text = transaction.amount.toString()
            categoryTagTv.text = transaction.categoryTag
        }
    }
}
