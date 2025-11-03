package com.rpl.fintrack.ui.write.income

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rpl.fintrack.database.Result
import com.rpl.fintrack.database.remote.request.WriteTransactionRequest
import com.rpl.fintrack.databinding.FragmentIncomeBinding
import com.rpl.fintrack.ui.factory.TransactionModelFactory
import com.rpl.fintrack.ui.factory.UserPrefModelFactory
import com.rpl.fintrack.ui.list.TransactionsActivity
import com.rpl.fintrack.ui.userDataStoreViewModel
import com.rpl.fintrack.ui.write.WriteViewModel
import com.rpl.fintrack.util.dateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding
    private val calendar = Calendar.getInstance()

    private val prefFactory: UserPrefModelFactory by lazy {
        UserPrefModelFactory.getInstance(requireContext())
    }

    private val userPrefViewModel: userDataStoreViewModel by viewModels {
        prefFactory
    }

    private val factory: TransactionModelFactory by lazy {
        TransactionModelFactory.getInstance(requireContext())
    }

    private val viewModel: WriteViewModel by viewModels {
        factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLayout()
        setupViewModelListener()
    }

    private fun setupLayout(){

        binding?.pgWriteIncome?.visibility = View.GONE
        binding?.tvNameCharLeft?.let { binding?.nameEditText?.setCharCountView(it) }
        binding?.tvDescriptionCharLeft?.let { binding?.descriptionEditText?.setCharCountView(it) }

        setFormattedDateTime()

        binding?.dateEditText?.setOnClickListener {
            showDateTimePicker()
        }

        binding?.dateInputLayout?.setEndIconOnClickListener {
            showDateTimePicker()
        }

        binding?.btnWriteIncome?.setOnClickListener{
            handleSubmitIncome()
        }
    }

    private fun setupViewModelListener(){
        viewModel.writeResult.observe(viewLifecycleOwner) { response ->
            when(response){
                is Result.Loading -> showLoading(true)
                is Result.Success -> handleWriteSuccess(response.data.message)
                is Result.Error -> handleWriteError(response.error)
            }
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding?.pgWriteIncome?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleWriteSuccess(message: String){
        showLoading(false)
        val intent = Intent(requireContext(), TransactionsActivity::class.java)
        AlertDialog.Builder(requireContext())
            .setTitle("Write Successful")
            .setMessage(message)
            .setPositiveButton("Continue") { dialog, _ ->
                dialog.dismiss()
                startActivity(intent)
            }
            .show()
    }

    private fun handleWriteError(errorMessage: String){
        showLoading(false)
        AlertDialog.Builder(requireContext())
            .setTitle("Write Failed")
            .setMessage(errorMessage)
            .setNegativeButton("Continue") { dialog, _ -> dialog.dismiss() }
            .show()
        Log.e("Income Fragment", "Error: $errorMessage")
    }

    private fun showDateTimePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)

                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        setFormattedDateTime()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun handleSubmitIncome() {
        lifecycleScope.launch {
            val binding = binding ?: return@launch
            val uid = userPrefViewModel.getUid().first()

            val inputDate = binding.dateEditText.text.toString()
            val timestampFormattedDate = dateUtils.parseDisplayDateToTimestamp(inputDate)

            val writeTransactionInfo = WriteTransactionRequest(
                date = timestampFormattedDate,
                uid = uid,
                amount = binding.amountEditText.text.toString().toIntOrNull(),
                name = binding.nameEditText.text.toString(),
                description = binding.descriptionEditText.text.toString(),
                type = "Income",
                category = binding.dropdownCategory.text.toString()
            )

            viewModel.writeTransaction(writeTransactionInfo)
        }
    }

    private fun setFormattedDateTime() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val rawTimestamp = sdf.format(calendar.time)
        val formatted = dateUtils.getDateString(rawTimestamp)
        binding?.dateEditText?.setText(formatted)
    }

}