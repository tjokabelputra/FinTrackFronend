package com.rpl.fintrack.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpl.fintrack.database.TransactionRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    fun clearTransactions() = viewModelScope.launch {
        transactionRepository.clearTransaction()
    }
}