package com.rpl.fintrack.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rpl.fintrack.database.TransactionRepository
import com.rpl.fintrack.database.Result
import com.rpl.fintrack.database.local.entity.TransactionEntity
import com.rpl.fintrack.database.remote.response.ErrorResponse
import com.rpl.fintrack.database.remote.response.TransactionsListResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ListsViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    val listResult = MutableLiveData<Result<TransactionsListResponse>>()

    fun getLists(uid: String, date: String) = viewModelScope.launch {
        listResult.postValue(Result.Loading)
        try {
            val response = transactionRepository.transactionsList(uid, date)
            when {
                response.isSuccessful -> {
                    val contentResponse = response.body()
                    if(contentResponse != null){
                        listResult.postValue(Result.Success(contentResponse))
                    }
                    else{
                        listResult.postValue(Result.Error("Empty Response Body"))
                    }
                }
                else -> {
                    val errorBodyString = response.errorBody()?.string()
                    if (errorBodyString != null) {
                        val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                        listResult.postValue(Result.Error(errorResponse.message ?: "Error parsing response"))
                    }
                    else {
                        listResult.postValue(Result.Error("Unknown Error"))
                    }
                }
            }
        }
        catch (e: HttpException) {
            val errorBodyString = e.response()?.errorBody()?.string()
            if (errorBodyString != null) {
                val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                listResult.postValue(Result.Error(errorResponse.message ?: "Error parsing response"))
            }
            else {
                listResult.postValue(Result.Error(e.message ?: "An unexpected error occurred"))
            }
        }
        catch (e: Exception) {
            listResult.postValue(Result.Error(e.message ?: "An unexpected error occurred"))
        }
    }

    fun addTransactions(transactions: List<TransactionEntity>) = viewModelScope.launch {
        transactionRepository.addTransactions(transactions)
    }

    fun getTransactions(date: String) = transactionRepository.getTransactions(date)
}