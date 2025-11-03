package com.rpl.fintrack.database.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rpl.fintrack.database.local.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `transaction` WHERE date(date) = :date ORDER BY date ASC")
    fun getTransactionsList(date: String): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM `transaction` WHERE tid = :tid")
    fun getTransactionDetail(tid: Int): LiveData<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("DELETE FROM `transaction` WHERE tid = :tid")
    suspend fun deleteTransaction(tid: Int)

    @Query("DELETE FROM `transaction`")
    suspend fun clearTransactions()
}