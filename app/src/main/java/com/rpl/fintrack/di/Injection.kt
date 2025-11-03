package com.rpl.fintrack.di

import android.content.Context
import com.rpl.fintrack.database.AccountRepository
import com.rpl.fintrack.database.TransactionRepository
import com.rpl.fintrack.database.local.room.TransactionDatabase
import com.rpl.fintrack.database.remote.retrofit.ApiConfig
import com.rpl.fintrack.database.userDataStore
import com.rpl.fintrack.database.userPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideAccountRepository(): AccountRepository{
        val apiService = ApiConfig.getAccountService()
        return AccountRepository.getInstance(apiService)
    }

    fun provideAccountDataStore(context: Context): userPreference{
        return userPreference.getInstance(context.userDataStore)
    }

    fun provideTransactionRepository(context: Context): TransactionRepository{
        val database = TransactionDatabase.getInstance(context)
        val pref = userPreference.getInstance(context.userDataStore)
        val user = runBlocking { pref.getUser().first() }
        val apiService = ApiConfig.getTransactionService(user.token)
        val dao = database.transactionDao()
        return TransactionRepository.getInstance(apiService, dao)
    }
}