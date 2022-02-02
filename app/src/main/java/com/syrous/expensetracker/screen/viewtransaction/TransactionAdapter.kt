package com.syrous.expensetracker.screen.viewtransaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syrous.expensetracker.databinding.LayoutTransactionHeaderViewHolderBinding
import com.syrous.expensetracker.databinding.LayoutTransactionItemViewHolderBinding
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.screen.viewtransaction.TransactionHeaderItem.TransactionHeader
import com.syrous.expensetracker.screen.viewtransaction.TransactionHeaderItem.TransactionItem
import com.syrous.expensetracker.screen.viewtransaction.TransactionViewHolder.TransactionHeaderViewHolder
import com.syrous.expensetracker.screen.viewtransaction.TransactionViewHolder.TransactionItemViewHolder
import com.syrous.expensetracker.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter : ListAdapter<TransactionHeaderItem, TransactionViewHolder>(callback) {
    private val dateFormatter = SimpleDateFormat(Constants.datePattern, Locale.getDefault())

    companion object {
        val callback = object : DiffUtil.ItemCallback<TransactionHeaderItem>() {
            override fun areItemsTheSame(
                oldItem: TransactionHeaderItem,
                newItem: TransactionHeaderItem
            ): Boolean = true

            override fun areContentsTheSame(
                oldItem: TransactionHeaderItem,
                newItem: TransactionHeaderItem
            ): Boolean = true
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            val binding = LayoutTransactionHeaderViewHolderBinding.inflate(layoutInflater)
            TransactionHeaderViewHolder(binding)
        } else {
            val binding = LayoutTransactionItemViewHolderBinding.inflate(layoutInflater)
            TransactionItemViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position) is TransactionHeader) 0
        else 1


    override fun onBindViewHolder(
        holderItem: TransactionViewHolder,
        position: Int
    ) {
        if(holderItem is TransactionHeaderViewHolder)
            holderItem.bind(getItem(position) as TransactionHeader, position, dateFormatter)
         else if(holderItem is TransactionItemViewHolder)
             holderItem.bind((getItem(position) as TransactionItem).userTransaction, position, dateFormatter)
    }
}


sealed class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class TransactionHeaderViewHolder(
        private val binding: LayoutTransactionHeaderViewHolderBinding
    ) : TransactionViewHolder(binding.root) {
        fun bind(header: TransactionHeader, position: Int, dateFormatter: SimpleDateFormat) {
            binding.apply {
                dateTv.text = dateFormatter.format(header.date)
            }
        }
    }

    class TransactionItemViewHolder(
        private val binding: LayoutTransactionItemViewHolderBinding
    ) : TransactionViewHolder(binding.root) {

        fun bind(transaction: UserTransaction, position: Int, dateFormatter: SimpleDateFormat) {
            binding.apply {
                categoryTv.text = transaction.categoryTag
                descriptionTv.text = transaction.description
                amountTv.text = "₹ ${transaction.amount}"
            }
        }
    }
}
