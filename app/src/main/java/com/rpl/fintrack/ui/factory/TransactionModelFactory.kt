package com.rpl.fintrack.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rpl.fintrack.database.TransactionRepository
import com.rpl.fintrack.di.Injection
import com.rpl.fintrack.ui.detail.DetailViewModel
import com.rpl.fintrack.ui.list.ListsViewModel
import com.rpl.fintrack.ui.settings.SettingsViewModel
import com.rpl.fintrack.ui.summary.SummaryViewModel
import com.rpl.fintrack.ui.write.WriteViewModel

class TransactionModelFactory private constructor(private val transactionRepository: TransactionRepository): ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ListsViewModel::class.java)){
            return ListsViewModel(transactionRepository) as T
        }
        if(modelClass.isAssignableFrom(DetailViewModel::class.java)){
            return DetailViewModel(transactionRepository) as T
        }
        if(modelClass.isAssignableFrom(WriteViewModel::class.java)){
            return WriteViewModel(transactionRepository) as T
        }
        if(modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            return SettingsViewModel(transactionRepository) as T
        }
        if(modelClass.isAssignableFrom(SummaryViewModel::class.java)){
            return SummaryViewModel(transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object{
        @Volatile
        private var instance: TransactionModelFactory? = null
        fun getInstance(context: Context): TransactionModelFactory =
            instance ?: synchronized(this){
                instance ?: TransactionModelFactory(Injection.provideTransactionRepository(context))
            }.also { instance = it }
    }
}