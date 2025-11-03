package com.rpl.fintrack.ui.detail

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rpl.fintrack.R
import com.rpl.fintrack.database.Result
import com.rpl.fintrack.databinding.ActivityDetailBinding
import com.rpl.fintrack.ui.factory.TransactionModelFactory
import com.rpl.fintrack.ui.list.TransactionsActivity
import com.rpl.fintrack.util.currencyUtils
import com.rpl.fintrack.util.dateUtils

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding

    private val factory: TransactionModelFactory by lazy {
        TransactionModelFactory.getInstance(this)
    }

    private val viewModel: DetailViewModel by viewModels{
        factory
    }

    private var tid: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.hide()
        binding?.pgDetail?.visibility = View.GONE

        tid = intent.getIntExtra(TID, -1)

        setupEdgetoEdge()
        fetchTransactionInfo()
        setupDeleteButton()
        setupViewModelObserver()
        setupFAB()
    }

    private fun setupFAB(){
        binding?.fabHome?.setOnClickListener{
            val intent = Intent(this, TransactionsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupEdgetoEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){ v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchTransactionInfo(){
        viewModel.getTransactionDetail(tid).observe(this) { transaction ->
            transaction?.let {
                binding?.apply {
                    tvNameContent.text = it.name
                    tvTypeContent.text = it.type
                    tvDateContent.text = dateUtils.getDateString(it.date)
                    tvCategoryContent.text = it.category
                    tvAmountContent.text = currencyUtils.formatRupiah(it.amount.toLong())
                    tvDescriptionContent.text = it.description
                }
            }
        }
    }

    private fun setupDeleteButton(){
        binding?.btnDelete?.setOnClickListener{
            handleDeleteButton()
        }
    }

    private fun handleDeleteButton(){
        viewModel.deleteTransaction(tid.toString())
    }

    private fun setupViewModelObserver(){
        viewModel.deleteResult.observe(this){ response ->
            when(response) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> handleDeleteSuccess(response.data.message)
                is Result.Error -> handleDeleteError(response.error)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.pgDetail?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleDeleteSuccess(message: String){
        showLoading(false)
        val intent = Intent(this, TransactionsActivity::class.java)
        AlertDialog.Builder(this)
            .setTitle("Delete Successful")
            .setMessage(message)
            .setPositiveButton("Continue") { dialog, _ ->
                dialog.dismiss()
                startActivity(intent)
            }
            .show()
    }

    private fun handleDeleteError(errorMessage: String){
        showLoading(false)
        AlertDialog.Builder(this)
            .setTitle("Delete Failed")
            .setMessage(errorMessage)
            .setNegativeButton("Continue") { dialog, _ -> dialog.dismiss() }
            .show()
        Log.e("Detail Activity", "Error: $errorMessage")
    }

    companion object{
        private const val TID ="transactionId"
    }
}