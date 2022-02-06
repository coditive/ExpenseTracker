package com.syrous.expensetracker.screen.release

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.syrous.expensetracker.R
import com.syrous.expensetracker.databinding.LayoutReleaseMainActivityBinding
import com.syrous.expensetracker.model.DashboardSubCategoryItem
import com.syrous.expensetracker.screen.addusertransaction.UserTransactionActivity
import com.syrous.expensetracker.screen.viewtransaction.TransactionAdapter
import com.syrous.expensetracker.service.enqueueSpreadSheetSyncWork
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ReleaseMainActivity : AppCompatActivity(), OnChartValueSelectedListener {

    private lateinit var binding: LayoutReleaseMainActivityBinding

    private val viewModel: ReleaseVMImpl by viewModels()

    private val transactionAdapter = TransactionAdapter()

    @Inject
    lateinit var workManager: WorkManager

    private lateinit var pieDataSet: PieDataSet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutReleaseMainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        viewModel.totalExpense.asLiveData().observe(this) { totalExpense ->
            if (totalExpense == null) binding.totalExpenseAmountTv.text = "₹ 0"
            else binding.totalExpenseAmountTv.text = "₹ $totalExpense"
        }

        viewModel.pieChartItemsList.asLiveData().observe(this) { dashboardCategoryItemList ->
            if (dashboardCategoryItemList.isNotEmpty())
                setupPieChartData(dashboardCategoryItemList)
        }

        binding.transactionList.apply {
            adapter = transactionAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
        }

        viewModel.transactionsList.asLiveData().observe(this) {
            val controller = AnimationUtils.loadLayoutAnimation(binding.transactionList.context, R.anim.layout_anim_slide_up)
            binding.transactionList.layoutAnimation = controller
            transactionAdapter.submitList(it)
            binding.transactionList.scheduleLayoutAnimation()
        }

        binding.addUserTransactionButton.setOnClickListener {
            startActivity(Intent(this, UserTransactionActivity::class.java))
        }

        binding.categoryPieChart.apply {

            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5F, 10F, 5F, 5F)

            dragDecelerationFrictionCoef = 0.95f

            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setOnChartValueSelectedListener(this@ReleaseMainActivity)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)

            holeRadius = 58f
            transparentCircleRadius = 61f

            setDrawCenterText(true)

            rotationAngle = 0F
            // enable rotation of the chart by touch
            // enable rotation of the chart by touch
            isRotationEnabled = false
            isHighlightPerTapEnabled = true
            setUsePercentValues(true)
            // add a selection listener
            animateY(1400, Easing.EaseInOutQuad)

            val l: Legend = legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            l.orientation = Legend.LegendOrientation.VERTICAL
            l.setDrawInside(false)
            l.xEntrySpace = 7f
            l.yEntrySpace = 0f
            l.yOffset = 0f

            // entry label styling

            // entry label styling
            setDrawEntryLabels(false)
        }

    }

    override fun onPause() {
        super.onPause()
        workManager.enqueueSpreadSheetSyncWork()
    }

    private fun setupPieChartData(dashboardCategoryItemList: List<DashboardSubCategoryItem>) {
        val totalExpense = binding.totalExpenseAmountTv.text.split(" ")[1].toInt()
        val pieDataEntry = mutableListOf<PieEntry>()

        for (item in dashboardCategoryItemList)
            pieDataEntry.add(
                PieEntry(
                    ((item.amountSpent.toFloat() / totalExpense.toFloat()) * 100),
                    item.itemName
                )
            )

        val colors = ArrayList<Int>()
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        pieDataSet = PieDataSet(pieDataEntry, "")
        pieDataSet.sliceSpace = 3f
        pieDataSet.colors = colors
        pieDataSet.selectionShift = 5f
        val data = PieData(pieDataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        binding.categoryPieChart.data = data
        binding.categoryPieChart.invalidate()
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        h?.x?.toInt()?.let { pieDataSet.getEntryForIndex(it).label }
            ?.let { viewModel.pieValueSelected(it) }
    }

    override fun onNothingSelected() {
        viewModel.nothingSelected()
    }
}