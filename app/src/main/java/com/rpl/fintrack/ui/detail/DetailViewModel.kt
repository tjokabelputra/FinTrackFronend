package com.rpl.fintrack.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rpl.fintrack.database.TransactionRepository
import com.rpl.fintrack.database.remote.response.MessageTransactionResponse
import com.rpl.fintrack.database.Result
import com.rpl.fintrack.database.remote.response.ErrorResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    val deleteResult = MutableLiveData<Result<MessageTransactionResponse>>()

    fun getTransactionDetail(tid: Int) = transactionRepository.getTransactionDetail(tid)

    fun deleteTransaction(tid: String) = viewModelScope.launch {
        deleteResult.postValue(Result.Loading)
        try{
            val response = transactionRepository.deleteTransaction(tid)
            transactionRepository.deleteTransaction(tid.toInt())
            when{
                response.isSuccessful -> {
                    val contentResponse = response.body()
                    if (contentResponse != null) {
                        deleteResult.postValue(Result.Success(contentResponse))
                    }
                    else {
                        deleteResult.postValue(Result.Error("Empty Response Body"))
                    }
                }
                else -> {
                    val errorBodyString = response.errorBody()?.string()
                    if (errorBodyString != null) {
                        val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                        deleteResult.postValue(Result.Error(errorResponse.message ?: "Error parsing response"))
                    }
                    else {
                        deleteResult.postValue(Result.Error("Unknown Error"))
                    }
                }
            }
        }
        catch (e: HttpException) {
            val errorBodyString = e.response()?.errorBody()?.string()
            if (errorBodyString != null) {
                val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                deleteResult.postValue(Result.Error(errorResponse.message ?: "Error parsing response"))
            }
            else {
                deleteResult.postValue(Result.Error(e.message ?: "An unexpected error occurred"))
            }
        }
        catch (e: Exception) {
            deleteResult.postValue(Result.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}