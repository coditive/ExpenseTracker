package com.syrous.expensetracker.screen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syrous.expensetracker.data.local.model.DBTransaction
import com.syrous.expensetracker.databinding.LayoutTransactionViewHolderBinding
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter : ListAdapter<UserTransaction, TransactionViewHolder>(callback) {
    private val dateFormatter = SimpleDateFormat(Constants.datePattern, Locale.getDefault())

    companion object {
        val callback = object : DiffUtil.ItemCallback<UserTransaction>() {
            override fun areItemsTheSame(
                oldItem: UserTransaction,
                newItem: UserTransaction
            ): Boolean = true

            override fun areContentsTheSame(
                oldItem: UserTransaction,
                newItem: UserTransaction
            ): Boolean = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutTransactionViewHolderBinding.inflate(layoutInflater)
        return TransactionViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position), position, dateFormatter)
    }
}

class TransactionViewHolder(
    private val binding: LayoutTransactionViewHolderBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(transaction: UserTransaction, position: Int, dateFormatter: SimpleDateFormat) {
        binding.apply {
            (position + 1).toString().also { serialNumber.text = it }
            if (transaction.category == Category.EXPENSE)
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

            date.text = dateFormatter.format(transaction.date)
            amountTv.text = transaction.amount.toString()
            categoryTagTv.text = transaction.categoryTag
        }
    }
}
