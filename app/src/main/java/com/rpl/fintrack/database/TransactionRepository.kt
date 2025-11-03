package com.rpl.fintrack.database

import com.rpl.fintrack.database.local.entity.TransactionEntity
import com.rpl.fintrack.database.local.room.TransactionDao
import com.rpl.fintrack.database.remote.request.WriteTransactionRequest
import com.rpl.fintrack.database.remote.retrofit.TransactionApiService

class TransactionRepository private constructor(
    private val transactionApiService: TransactionApiService,
    private val transactionDao: TransactionDao
){
    suspend fun writeTransaction(request: WriteTransactionRequest) = transactionApiService.writeTransaction(request)

    suspend fun transactionsList(uid: String, date: String) = transactionApiService.transactionsList(uid, date)

    suspend fun transactionsSummary(year: Int, month: Int) = transactionApiService.transactionsSummary(year, month)

    suspend fun deleteTransaction(tid: String) = transactionApiService.deleteTransaction(tid)

    suspend fun addTransactions(transactions: List<TransactionEntity>) = transactionDao.insertTransactions(transactions)

    fun getTransactions(date: String) = transactionDao.getTransactionsList(date)

    fun getTransactionDetail(tid: Int) = transactionDao.getTransactionDetail(tid)

    suspend fun deleteTransaction(tid: Int) = transactionDao.deleteTransaction(tid)

    suspend fun clearTransaction() = transactionDao.clearTransactions()

    companion object{
        @Volatile
        private var instance: TransactionRepository? = null
        fun getInstance(
            transactionApiService: TransactionApiService,
            transactionDao: TransactionDao
        ): TransactionRepository =
            instance ?: synchronized(this) {
                instance ?: TransactionRepository(transactionApiService, transactionDao)
            }.also { instance = it }
    }
}