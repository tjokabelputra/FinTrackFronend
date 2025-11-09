package com.rpl.fintrack.ui.summary

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.rpl.fintrack.database.Result
import com.rpl.fintrack.database.remote.response.ExpensesItem
import com.rpl.fintrack.database.remote.response.IncomeItem
import com.rpl.fintrack.database.remote.response.MonthlySummaryResponse
import com.rpl.fintrack.database.remote.response.TotalItem
import com.rpl.fintrack.databinding.FragmentSummaryBinding
import com.rpl.fintrack.ui.factory.TransactionModelFactory
import com.rpl.fintrack.util.dateUtils
import java.sql.Timestamp
import java.util.Calendar

class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    private val factory: TransactionModelFactory by lazy {
        TransactionModelFactory.getInstance(requireContext())
    }

    private val viewModel: SummaryViewModel by viewModels { factory }

    private val calendar: Calendar = Calendar.getInstance()
    private var date: Timestamp? = null
    private var displayDate: String? = null

    private val yearMonthQuery = MutableLiveData<Pair<Int, Int>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pgSummary.visibility = View.GONE

        setupTopMenu()
        setupObservers()
    }

    private fun setupTopMenu() {
        binding.ivRight.visibility = View.GONE

        calendar.add(Calendar.MONTH, -1)
        updateDate()

        binding.ivLeft.setOnClickListener { prevMonth() }
        binding.ivRight.setOnClickListener { nextMonth() }
    }

    private fun prevMonth() {
        calendar.add(Calendar.MONTH, -1)
        updateDate()
    }

    private fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        updateDate()
    }

    private fun updateDate() {
        date = Timestamp(calendar.time.time)
        displayDate = dateUtils.formatTimestampToMonth(date!!)
        binding.tvDate.text = displayDate
        updateRightArrowVisibility()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        yearMonthQuery.value = Pair(year, month)
    }

    private fun updateRightArrowVisibility() {
        val current = Calendar.getInstance()

        val maxYear = current.get(Calendar.YEAR)
        val maxMonth = current.get(Calendar.MONTH) - 1

        val displayedYear = calendar.get(Calendar.YEAR)
        val displayedMonth = calendar.get(Calendar.MONTH)

        val showRightArrow = when {
            displayedYear < maxYear -> true
            displayedYear == maxYear && displayedMonth < maxMonth -> true
            else -> false
        }

        binding.ivRight.visibility = if (showRightArrow) View.VISIBLE else View.GONE
    }

    private fun setupObservers() {
        viewModel.summaryResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> handleSummarySuccess(response.data)
                is Result.Error -> handleSummaryError(response.error)
            }
        }

        yearMonthQuery.observe(viewLifecycleOwner) { (year, month) ->
            viewModel.getSummary(year, month)
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.pgSummary.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleSummarySuccess(response: MonthlySummaryResponse){
        val incomeList = response.income.orEmpty().filterNotNull().reversed()
        val expensesList = response.expenses.orEmpty().filterNotNull().reversed()
        val summaryList = response.total.orEmpty().filterNotNull().reversed()

        val hasData = incomeList.isNotEmpty() || expensesList.isNotEmpty() || summaryList.isNotEmpty()

        if (hasData) {
            handleIncomeBar(incomeList)
            handleExpensesBar(expensesList)
            handleSummaryBar(summaryList)
        } else {
            showLoading(false)
            binding.incomeBar.clear()
            binding.expensesBar.clear()
            binding.summaryBar.clear()
        }
    }

    private fun handleIncomeBar(income: List<IncomeItem?>?) {
        showLoading(false)

        val nonNullIncome = income.orEmpty().filterNotNull()
        if (nonNullIncome.isEmpty()) {
            binding.incomeBar.clear()
            Toast.makeText(requireContext(), "No income data available", Toast.LENGTH_SHORT).show()
            return
        }

        val barCount = nonNullIncome.size
        val heightInDp = 70 * barCount
        val heightInPx = (heightInDp * resources.displayMetrics.density).toInt()
        binding.incomeBar.layoutParams.height = heightInPx
        binding.incomeBar.requestLayout()

        val entries = nonNullIncome.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.totalAmount?.toFloat() ?: 0f)
        }

        val dataSet = BarDataSet(entries, "Income by Category").apply {
            color = Color.parseColor("#28A745")
            valueTextSize = 14f
            valueTextColor = Color.BLACK
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        val barChart = binding.incomeBar
        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setFitBars(true)

        barChart.axisRight.isEnabled = false

        barChart.axisLeft.apply {
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawAxisLine(false)
            axisMinimum = 0f
            textColor = Color.BLACK
            textSize = 14f
        }

        barChart.xAxis.apply {
            granularity = 1f
            textColor = Color.BLACK
            textSize = 14f
            setDrawGridLines(false)
            position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            axisMinimum = -0.4f
            valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                nonNullIncome.map { it.category ?: "Unknown" }
            )
        }

        barChart.setExtraOffsets(0f, 0f, 70f, 0f)

        barChart.animateX(1200)
        barChart.invalidate()
    }

    private fun handleExpensesBar(expenses: List<ExpensesItem?>?){
        val nonNullExpenses = expenses.orEmpty().filterNotNull()
        if (nonNullExpenses.isEmpty()) {
            binding.expensesBar.clear()
            Toast.makeText(requireContext(), "No expenses data available", Toast.LENGTH_SHORT).show()
            return
        }

        val barCount = nonNullExpenses.size
        val heightInDp = 70 * barCount
        val heightInPx = (heightInDp * resources.displayMetrics.density).toInt()
        binding.expensesBar.layoutParams.height = heightInPx
        binding.expensesBar.requestLayout()

        val entries = nonNullExpenses.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.totalAmount?.toFloat() ?: 0f)
        }

        val dataSet = BarDataSet(entries, "Income by Category").apply {
            color = Color.parseColor("#DC3545")
            valueTextSize = 14f
            valueTextColor = Color.BLACK
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        val barChart = binding.expensesBar
        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setFitBars(true)

        barChart.axisRight.isEnabled = false

        barChart.axisLeft.apply {
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawAxisLine(false)
            axisMinimum = 0f
            textColor = Color.BLACK
            textSize = 14f
        }

        barChart.xAxis.apply {
            granularity = 1f
            textColor = Color.BLACK
            textSize = 14f
            setDrawGridLines(false)
            position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            axisMinimum = -0.4f
            valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                nonNullExpenses.map { it.category ?: "Unknown" }
            )
        }

        barChart.setExtraOffsets(0f, 0f, 70f, 0f)

        barChart.animateX(1200)
        barChart.invalidate()
    }

    private fun handleSummaryBar(summary: List<TotalItem?>?) {
        val nonNullSummary = summary.orEmpty().filterNotNull()

        // Clear old data and reset
        val barChart = binding.summaryBar
        barChart.clear()
        barChart.invalidate()

        if (nonNullSummary.isEmpty()) {
            Toast.makeText(requireContext(), "No summary data available", Toast.LENGTH_SHORT).show()
            return
        }

        val entries = nonNullSummary.mapIndexed { index, item ->
            val value = item.totalType?.toFloat() ?: 0f
            BarEntry(index.toFloat(), kotlin.math.abs(value))
        }

        val colors = nonNullSummary.map { item ->
            when (item.type) {
                "Income" -> Color.parseColor("#28A745")
                "Expenses" -> Color.parseColor("#DC3545")
                "Net Total" -> {
                    val total = item.totalType ?: 0
                    if (total >= 0) Color.parseColor("#28A745") else Color.parseColor("#DC3545")
                }
                else -> Color.GRAY
            }
        }

        val dataSet = BarDataSet(entries, "Summary").apply {
            setColors(colors)
            valueTextSize = 14f
            valueTextColor = Color.BLACK
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setFitBars(true)
        barChart.axisRight.isEnabled = false

        barChart.axisLeft.apply {
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawAxisLine(false)
            axisMinimum = 0f
            textColor = Color.BLACK
            textSize = 14f
        }

        barChart.xAxis.apply {
            granularity = 1f
            textColor = Color.BLACK
            textSize = 14f
            setDrawGridLines(false)
            position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

            axisMinimum = -0.5f
            axisMaximum = nonNullSummary.size.toFloat() - 0.5f

            valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                nonNullSummary.map { it.type ?: "Unknown" }
            )
        }

        barChart.setExtraOffsets(0f, 0f, 70f, 0f)

        barChart.notifyDataSetChanged()
        barChart.invalidate()
        barChart.animateX(1200)
    }


    private fun handleSummaryError(errorMessage: String){
        showLoading(false)
        Log.e("Summary Fragment", "Error: $errorMessage")
        Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
