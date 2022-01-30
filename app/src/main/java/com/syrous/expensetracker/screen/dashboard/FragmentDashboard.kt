package com.syrous.expensetracker.screen.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.syrous.expensetracker.R
import com.syrous.expensetracker.databinding.LayoutDashboardScreenBinding
import com.syrous.expensetracker.model.DashboardCategoryItem
import com.syrous.expensetracker.screen.CarouselAdapter

class FragmentDashboard : Fragment() {

    private lateinit var binding: LayoutDashboardScreenBinding
    private val carouselAdapter = CarouselAdapter()

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
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    }
                })
            }

        }

        val dashboardCategoryItemList = mutableListOf<DashboardCategoryItem>()

        dashboardCategoryItemList.add(
            DashboardCategoryItem(
                "Food",
                R.drawable.ic_baseline_add_24,
                12345
            )
        )
        dashboardCategoryItemList.add(
            DashboardCategoryItem(
                "Food",
                R.drawable.ic_baseline_add_24,
                12345
            )
        )
        dashboardCategoryItemList.add(
            DashboardCategoryItem(
                "Food",
                R.drawable.ic_baseline_add_24,
                12345
            )
        )
        dashboardCategoryItemList.add(
            DashboardCategoryItem(
                "Food",
                R.drawable.ic_baseline_add_24,
                12345
            )
        )
        dashboardCategoryItemList.add(
            DashboardCategoryItem(
                "Food",
                R.drawable.ic_baseline_add_24,
                12345
            )
        )

        carouselAdapter.submitList(dashboardCategoryItemList)

    }
}