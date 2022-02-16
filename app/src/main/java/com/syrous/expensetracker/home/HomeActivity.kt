package com.syrous.expensetracker.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.syrous.expensetracker.R
import com.syrous.expensetracker.model.SubCategoryItem
import com.syrous.expensetracker.addusertransaction.UserTransactionBottomSheet
import com.syrous.expensetracker.databinding.LayoutHomeActivityBinding
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.screen.SignInActivity
import com.syrous.expensetracker.service.enqueueSpreadSheetSyncWork
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.getFormattedString
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), OnChartValueSelectedListener {

    private lateinit var binding: LayoutHomeActivityBinding

    private val viewModel: HomeVMImpl by viewModels()

    private val transactionAdapter = TransactionAdapter()

    @Inject
    lateinit var workManager: WorkManager

    private lateinit var pieDataSet: PieDataSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutHomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        viewModel.totalExpense.asLiveData().observe(this) { totalExpense ->
            if (totalExpense == null) {
                binding.totalExpenseAmountTv.text = "₹ 0"
                hideAllViews()
                binding.homeScreenAnim.visibility = View.VISIBLE
                binding.homeScreenAnim.playAnimation()
            } else {
                binding.totalExpenseAmountTv.text = "₹ ${totalExpense.toString().getFormattedString()}"
                showAllViews()
                binding.homeScreenAnim.visibility = View.GONE
            }
        }

        viewModel.totalIncome.asLiveData().observe(this) { totalIncome ->
            if (totalIncome == null) {
                binding.totalIncomeAmountTv.text = "₹ 0"
                binding.apply {
                    totalIncomeAmountTv.visibility = View.GONE
                    totalIncomeHeadTv.visibility = View.GONE
                    categorySwitcher.visibility = View.GONE
                }
            } else {
                binding.totalIncomeAmountTv.text = "₹ ${totalIncome.toString().getFormattedString()}"
                binding.apply {
                    totalIncomeAmountTv.visibility = View.VISIBLE
                    totalIncomeHeadTv.visibility = View.VISIBLE
                    categorySwitcher.visibility = View.VISIBLE
                }
            }
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
            val controller = AnimationUtils.loadLayoutAnimation(
                binding.transactionList.context,
                R.anim.layout_anim_slide_up
            )
            binding.transactionList.layoutAnimation = controller
            transactionAdapter.submitList(it)
            binding.transactionList.scheduleLayoutAnimation()
        }

        setupSpeedDialFab()

        binding.categorySwitcher.setOnCheckedChangeListener { isChecked ->
            if (isChecked)
                viewModel.categorySwitched(Category.INCOME)
            else
                viewModel.categorySwitched(Category.EXPENSE)
        }

        binding.categoryPieChart.apply {

            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5F, 10F, 5F, 5F)

            dragDecelerationFrictionCoef = 0.95f

            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setOnChartValueSelectedListener(this@HomeActivity)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)

            holeRadius = 58f
            transparentCircleRadius = 61f

            setDrawCenterText(true)

            rotationAngle = 0F
            isRotationEnabled = false
            isHighlightPerTapEnabled = true
            setUsePercentValues(true)
            animateY(1400, Easing.EaseInOutQuad)

            val l: Legend = legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            l.orientation = Legend.LegendOrientation.VERTICAL
            l.setDrawInside(false)
            l.xEntrySpace = 7f
            l.yEntrySpace = 0f
            l.yOffset = 0f
            setEntryLabelTypeface(ResourcesCompat.getFont(context, R.font.poppins_semibold))
            setDrawEntryLabels(false)
        }

    }

    override fun onPause() {
        super.onPause()
        workManager.enqueueSpreadSheetSyncWork()
    }

    private fun setupPieChartData(categoryItemList: List<SubCategoryItem>) {
        var totalExpense = 0
        categoryItemList.forEach {
            totalExpense += it.amountSpent
        }

        val pieDataEntry = mutableListOf<PieEntry>()

        for (item in categoryItemList)
            pieDataEntry.add(
                PieEntry(
                    ((item.amountSpent.toFloat() / totalExpense.toFloat()) * 100),
                    item.itemName
                )
            )

        val colors = ArrayList<Int>()
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        pieDataSet = PieDataSet(pieDataEntry, Constants.emptyString)
        pieDataSet.sliceSpace = 3f
        pieDataSet.colors = colors
        pieDataSet.selectionShift = 5f
        val data = PieData(pieDataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        data.setValueFormatter(object : ValueFormatter() {
            override fun getPieLabel(value: Float, pieEntry: PieEntry?): String =
                if (value == 0f) ""
                else value.roundToInt().toString() + "%"
        })

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


    fun displaySuccessSnackbar(category: String) {
        Snackbar.make(binding.root, "$category is stored successfully", Snackbar.LENGTH_SHORT)
            .show()
    }

    fun displayFailureSnackbar(message: String) {
        Snackbar.make(binding.root, "Failed due to $message", Snackbar.LENGTH_SHORT).show()
    }

    private fun setupSpeedDialFab() {
        binding.speedDial.apply {
            addActionItem(
                SpeedDialActionItem.Builder(R.id.expense_fab, R.drawable.ic_expense)
                    .setFabBackgroundColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.color_surface
                        )
                    )
                    .setFabImageTintColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.dark_blue
                        )
                    ).create()
            )
            addActionItem(
                SpeedDialActionItem.Builder(R.id.income_fab, R.drawable.ic_income)
                    .setFabBackgroundColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.color_surface
                        )
                    )
                    .setFabImageTintColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.dark_blue
                        )
                    )
                    .create()
            )
            addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.export_fab,
                    R.drawable.ic_baseline_import_export_24
                )
                    .setFabBackgroundColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.color_surface
                        )
                    )
                    .setFabImageTintColor(
                        ContextCompat.getColor(
                            this@HomeActivity,
                            R.color.dark_blue
                        )
                    )
                    .create()
            )

            setOnActionSelectedListener {
                return@setOnActionSelectedListener when (it.id) {
                    R.id.expense_fab -> {
                        val userTransactionDialog = UserTransactionBottomSheet()
                        val bundle = Bundle()
                        bundle.putString(Constants.category, Constants.expense)
                        userTransactionDialog.arguments = bundle
                        userTransactionDialog.show(supportFragmentManager, "UserTransaction")
                        close()
                        true
                    }
                    R.id.income_fab -> {
                        val userTransactionDialog = UserTransactionBottomSheet()
                        val bundle = Bundle()
                        bundle.putString(Constants.category, Constants.income)
                        userTransactionDialog.arguments = bundle
                        userTransactionDialog.show(supportFragmentManager, "UserTransaction")
                        close()
                        true
                    }
                    R.id.export_fab -> {
                        startActivity(Intent(this@HomeActivity, SignInActivity::class.java))
                        close()
                        false
                    }
                    else -> {
                        close()
                        true
                    }
                }
            }
        }
    }

    private fun hideAllViews() {
        binding.apply {
            titleLayout.visibility = View.GONE
            transactionHeadTv.visibility = View.GONE
            transactionList.visibility = View.GONE
        }
    }

    private fun showAllViews() {
        binding.apply {
            titleLayout.visibility = View.VISIBLE
            transactionHeadTv.visibility = View.VISIBLE
            transactionList.visibility = View.VISIBLE
        }
    }

}