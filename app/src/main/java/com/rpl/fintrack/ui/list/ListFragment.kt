package com.rpl.fintrack.ui.list

import android.app.DatePickerDialog
import android.content.Intent
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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.LinearLayoutManager
import com.rpl.fintrack.database.Result
import com.rpl.fintrack.database.local.entity.TransactionEntity
import com.rpl.fintrack.database.remote.response.TransactionsListResponse
import com.rpl.fintrack.databinding.FragmentListBinding
import com.rpl.fintrack.ui.adapter.TransactionsAdapter
import com.rpl.fintrack.ui.factory.TransactionModelFactory
import com.rpl.fintrack.ui.factory.UserPrefModelFactory
import com.rpl.fintrack.ui.userDataStoreViewModel
import com.rpl.fintrack.ui.write.WriteActivity
import com.rpl.fintrack.util.currencyUtils
import com.rpl.fintrack.util.dateUtils
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.Calendar

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private var date: Timestamp? = null
    private var displayDate: String? = null

    private val factory: TransactionModelFactory by lazy {
        TransactionModelFactory.getInstance(requireContext())
    }

    private val viewModel: ListsViewModel by viewModels {
        factory
    }

    private val prefFactory: UserPrefModelFactory by lazy {
        UserPrefModelFactory.getInstance(requireContext())
    }

    private val userPrefViewModel: userDataStoreViewModel by viewModels {
        prefFactory
    }

    private val transactionsAdapter by lazy { TransactionsAdapter() }

    private val dateQuery = MutableLiveData<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pgList.visibility = View.GONE

        setupAdapter()
        setupViewModelObserver()
        setupDateBar()
        fetchTransactionsList()
        setupWriteTransaction()
    }

    private fun setupAdapter(){
        binding.rvTransactionsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionsAdapter
        }
    }

    private fun setupViewModelObserver(){
        viewModel.listResult.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> handleListSuccess(response.data)
                is Result.Error -> handleListError(response.error)
            }
        })

        dateQuery.switchMap { dateString ->
            viewModel.getTransactions(dateString)
        }.observe(viewLifecycleOwner) { transactions ->
            transactionsAdapter.submitList(transactions)

            countSummaries(transactions)

            if (transactions.isEmpty()) {
                binding.rvTransactionsList.visibility = View.GONE
                binding.tvEmptyMessage.visibility = View.VISIBLE
            }
            else {
                binding.rvTransactionsList.visibility = View.VISIBLE
                binding.tvEmptyMessage.visibility = View.GONE
            }
        }
    }

    private fun setupDateBar() {
        date = dateUtils.getCurrentDate()
        displayDate = dateUtils.formatTimestampToDate(date!!)
        binding.tvDate.text = displayDate

        binding.ivLeft.setOnClickListener { prevDay() }
        binding.ivRight.setOnClickListener { nextDay() }
        binding.tvDate.setOnClickListener { openDatePicker() }

        dateQuery.value = dateUtils.getDate(date!!)
    }

    private fun fetchTransactionsList(){
        lifecycleScope.launch {
            userPrefViewModel.getUid().collect { uid ->
                val date = dateUtils.getDate(date!!)
                viewModel.getLists(uid, date)
            }
        }
    }

    private fun setupWriteTransaction(){
        binding.fabAddTransaction.setOnClickListener{
            val intent = Intent(context, WriteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pgList.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleListSuccess(response: TransactionsListResponse) {
        showLoading(false)
        val transactions = response.transactions?.filterNotNull()?.map { item ->
            TransactionEntity(
                tid = item.tid?.toString()?.toIntOrNull() ?: 0,
                uid = item.uid ?: "",
                type = item.type ?: "",
                name = item.name ?: "",
                category = item.category ?: "",
                date = dateUtils.utcToLocalString(item.date ?: ""),
                amount = item.amount ?: 0.0f,
                description = item.description ?: ""
            )
        } ?: emptyList()
        viewModel.addTransactions(transactions)
    }

    private fun countSummaries(transactions: List<TransactionEntity>){
        var income = 0.0f
        var expenses = 0.0f
        val net: Float
        for (transaction in transactions){
            if(transaction.type == "Income"){
                income += transaction.amount
            }
            else{
                expenses += transaction.amount
            }
        }
        binding.tvIncomeAmount.text = currencyUtils.formatRupiah(income.toLong())
        binding.tvExpensesAmount.text = currencyUtils.formatRupiah(expenses.toLong())
        if(income > expenses){
            net = income - expenses
            binding.tvNetAmount.text = currencyUtils.formatRupiah(net.toLong())
            binding.tvNetAmount.setTextColor(Color.parseColor("#28A745"))
        }
        else{
            net = expenses - income
            binding.tvNetAmount.text = currencyUtils.formatRupiah(net.toLong())
            binding.tvNetAmount.setTextColor(Color.parseColor("#DC3545"))
        }
    }

    private fun handleListError(errorMessage: String){
        showLoading(false)
        Log.e("List Fragment", "Error: $errorMessage")
        Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
    }

    private fun updateDate(){
        binding.tvDate.text = dateUtils.formatTimestampToDate(date!!)
        fetchTransactionsList()
        dateQuery.value = dateUtils.getDate(date!!)
    }

    private fun prevDay() {
        date = dateUtils.subtractOneDay(date!!)
        updateDate()
    }

    private fun nextDay() {
        date = dateUtils.addOneDay(date!!)
        updateDate()
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date?.time ?: System.currentTimeMillis()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val pickedCalendar = Calendar.getInstance()
                pickedCalendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
                date = Timestamp(pickedCalendar.timeInMillis)
                updateDate()
            },
            year,
            month,
            day
        )
        datePicker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
