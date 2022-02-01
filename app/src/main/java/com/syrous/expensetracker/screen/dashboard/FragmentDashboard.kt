package com.syrous.expensetracker.screen.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.syrous.expensetracker.databinding.LayoutDashboardScreenBinding
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.screen.CarouselAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentDashboard : Fragment() {

    private lateinit var binding: LayoutDashboardScreenBinding
    private val carouselAdapter = CarouselAdapter()

    private val viewModel: DashboardVMImpl by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutDashboardScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            categoryCarouselVp.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = carouselAdapter
                val helper: SnapHelper = LinearSnapHelper()
                helper.attachToRecyclerView(this)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        val position =
                            (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                        if (position != -1) {
                            viewModel.setCurrentCategoryItemId(carouselAdapter.currentList[position].itemId)
                        }
                    }
                })

            }

            categoryWeeklyExpenseChart.apply {
                isDragEnabled = false
                isClickable = false
                setTouchEnabled(false)
                setScaleEnabled(false)
                setPinchZoom(false)
                description.isEnabled = false
                setDrawGridBackground(false)
                xAxis.apply {
                    setDrawAxisLine(false)
                    granularity = 1f
                    setDrawGridLines(false)
                    position = XAxis.XAxisPosition.BOTTOM
                }
                axisLeft.setDrawAxisLine(false)
                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        }

        viewModel.topCategoriesList.asLiveData()
            .observe(viewLifecycleOwner) { dashboardCategoryList ->
                if (dashboardCategoryList.isNotEmpty())
                    viewModel.setCurrentCategoryItemId(dashboardCategoryList[0].itemId)
                carouselAdapter.submitList(dashboardCategoryList)
            }

        viewModel.totalExpense.asLiveData().observe(viewLifecycleOwner) { totalExpense ->
            binding.totalExpenseAmountTv.text = "₹ $totalExpense"
        }

        viewModel.categorizedUserTransactionList.asLiveData().observe(viewLifecycleOwner) {
            setUpChartData(it)
        }
    }

    private fun setUpChartData(userTransactionList: List<UserTransaction>) {
        if (userTransactionList.isNotEmpty()) {
            val lineDataSetEntryList = mutableListOf<Entry>()
            for (i in 1..6)
                if (i < userTransactionList.size)
                    lineDataSetEntryList.add(
                        Entry(
                            i.toFloat(),
                            userTransactionList[i].amount.toFloat()
                        )
                    )
                else
                    lineDataSetEntryList.add(
                        Entry(
                            i.toFloat(),
                            0f
                        )
                    )


            val lineDataSet = LineDataSet(lineDataSetEntryList, "Analytics")
            lineDataSet.axisDependency = YAxis.AxisDependency.LEFT

            binding.categoryWeeklyExpenseChart.data = LineData(lineDataSet)
            binding.categoryWeeklyExpenseChart.invalidate()

        }
    }

}



