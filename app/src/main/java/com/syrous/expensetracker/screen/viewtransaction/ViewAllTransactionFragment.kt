package com.syrous.expensetracker.screen.viewtransaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.syrous.expensetracker.databinding.LayoutViewTransactionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewAllTransactionFragment: Fragment() {

    private lateinit var binding: LayoutViewTransactionsBinding

    private val transactionAdapter = TransactionAdapter()

    private val categoryTagAdapter = CategoryTagAdapter()

    private val viewModel: ViewTransactionVMImpl by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutViewTransactionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            categoryTagRecyclerView.apply {
                adapter = categoryTagAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }

            transactionRecyclerView.apply {
                adapter = transactionAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        viewModel.transactionsList.asLiveData().observe(viewLifecycleOwner){
            transactionAdapter.submitList(it)
        }

        viewModel.categoryTagList.asLiveData().observe(viewLifecycleOwner) {
            categoryTagAdapter.submitList(it)
        }

    }
}