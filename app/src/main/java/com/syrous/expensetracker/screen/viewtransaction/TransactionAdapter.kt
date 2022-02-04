package com.syrous.expensetracker.screen.viewtransaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syrous.expensetracker.R
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
    private val headerFormatter = SimpleDateFormat(
        "dd MMMM", Locale.getDefault()
    )

    companion object {
        val dateFormatter = SimpleDateFormat(Constants.datePattern)
        val callback = object : DiffUtil.ItemCallback<TransactionHeaderItem>() {
            override fun areItemsTheSame(
                oldItem: TransactionHeaderItem,
                newItem: TransactionHeaderItem
            ): Boolean = if (oldItem is TransactionHeader && newItem is TransactionHeader) {
                dateFormatter.format(oldItem.date) == dateFormatter.format(newItem.date)
            } else if (oldItem is TransactionItem && newItem is TransactionItem) {
                oldItem.userTransaction.id == newItem.userTransaction.id
            } else true

            override fun areContentsTheSame(
                oldItem: TransactionHeaderItem,
                newItem: TransactionHeaderItem
            ): Boolean = if (oldItem is TransactionHeader && newItem is TransactionHeader) {
                dateFormatter.format(oldItem.date) == dateFormatter.format(newItem.date)
            } else if (oldItem is TransactionItem && newItem is TransactionItem) {
                oldItem.userTransaction.categoryTag == newItem.userTransaction.categoryTag && oldItem.userTransaction.amount == newItem.userTransaction.amount
            } else true
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            val binding =
                LayoutTransactionHeaderViewHolderBinding.inflate(layoutInflater, parent, false)
            TransactionHeaderViewHolder(binding)
        } else {
            val binding =
                LayoutTransactionItemViewHolderBinding.inflate(layoutInflater, parent, false)
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
        if (holderItem is TransactionHeaderViewHolder)
            holderItem.bind(getItem(position) as TransactionHeader, headerFormatter)
        else if (holderItem is TransactionItemViewHolder)
            holderItem.bind(
                (getItem(position) as TransactionItem).userTransaction,
                position,
                dateFormatter
            )
    }
}


sealed class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class TransactionHeaderViewHolder(
        private val binding: LayoutTransactionHeaderViewHolderBinding
    ) : TransactionViewHolder(binding.root) {
        fun bind(header: TransactionHeader, dateFormatter: SimpleDateFormat) {
            binding.apply {
                val calendar = Calendar.getInstance()
                val currentDate = dateFormatter.format(calendar.timeInMillis)
                calendar.add(Calendar.DATE, -1)
                val previousDate = dateFormatter.format(calendar.timeInMillis)
                val headerDate = dateFormatter.format(header.date)
                dateTv.text = when {
                    currentDate == headerDate -> "Today"
                    previousDate == headerDate -> "Yesterday"
                    else -> headerDate
                }
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
                val iconResId = when (transaction.categoryTag) {
                    "Food" -> R.raw.food
                    "MF" -> R.raw.rupee_coin
                    "Medical/Health" -> R.raw.medical_syringe
                    "Gifts" -> R.raw.gift
                    "Home" -> R.raw.home_icon_loading
                    "Personal" -> R.raw.profile
                    else -> null
                }
                if (iconResId != null)
                    iconAnimation.setAnimation(iconResId)
            }
        }
    }
}
