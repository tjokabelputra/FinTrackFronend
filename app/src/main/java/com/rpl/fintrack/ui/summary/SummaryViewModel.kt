package com.rpl.fintrack.ui.summary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rpl.fintrack.database.Result
import com.rpl.fintrack.database.TransactionRepository
import com.rpl.fintrack.database.remote.response.ErrorResponse
import com.rpl.fintrack.database.remote.response.MonthlySummaryResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SummaryViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    val summaryResult = MutableLiveData<Result<MonthlySummaryResponse>>()

    fun getSummary(year: Int, month: Int) = viewModelScope.launch {
        summaryResult.postValue(Result.Loading)
        try{
            val response = transactionRepository.transactionsSummary(year, month)
            when {
                response.isSuccessful -> {
                    val contentResponse = response.body()
                    if(contentResponse != null){
                        summaryResult.postValue(Result.Success(contentResponse))
                    }
                    else{
                        summaryResult.postValue(Result.Error("Empty Response Body"))
                    }
                }
                else -> {
                    val errorBodyString = response.errorBody()?.string()
                    if (errorBodyString != null) {
                        val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                        summaryResult.postValue(Result.Error(errorResponse.message ?: "Error parsing response"))
                    }
                    else {
                        summaryResult.postValue(Result.Error("Unknown Error"))
                    }
                }
            }
        }
        catch (e: HttpException) {
            val errorBodyString = e.response()?.errorBody()?.string()
            if (errorBodyString != null) {
                val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                summaryResult.postValue(Result.Error(errorResponse.message ?: "Error parsing response"))
            }
            else {
                summaryResult.postValue(Result.Error(e.message ?: "An unexpected error occurred"))
            }
        }
        catch (e: Exception) {
            summaryResult.postValue(Result.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}