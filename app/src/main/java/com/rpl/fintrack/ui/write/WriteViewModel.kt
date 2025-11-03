package com.rpl.fintrack.ui.write

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rpl.fintrack.database.TransactionRepository
import com.rpl.fintrack.database.remote.request.WriteTransactionRequest
import kotlinx.coroutines.launch
import com.rpl.fintrack.database.Result
import com.rpl.fintrack.database.remote.response.ErrorResponse
import com.rpl.fintrack.database.remote.response.MessageTransactionResponse
import retrofit2.HttpException

class WriteViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    val writeResult = MutableLiveData<Result<MessageTransactionResponse>>()

    fun writeTransaction(request: WriteTransactionRequest) = viewModelScope.launch {
        writeResult.postValue(Result.Loading)
        try{
            val response = transactionRepository.writeTransaction(request)
            when {
                response.isSuccessful -> {
                    val contentResponse = response.body()
                    if(contentResponse != null){
                        writeResult.postValue(Result.Success(contentResponse))
                    }
                    else{
                        writeResult.postValue(Result.Error("Empty Response Body"))
                    }
                }
                else -> {
                    val errorBodyString = response.errorBody()?.string()
                    if (errorBodyString != null) {
                        val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                        writeResult.postValue(Result.Error(errorResponse.message ?: "Error parsing response"))
                    }
                    else {
                        writeResult.postValue(Result.Error("Unknown Error"))
                    }
                }
            }
        }
        catch (e: HttpException) {
            val errorBodyString = e.response()?.errorBody()?.string()
            if (errorBodyString != null) {
                val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                writeResult.postValue(Result.Error(errorResponse.message ?: "Error parsing response"))
            }
            else {
                writeResult.postValue(Result.Error(e.message ?: "An unexpected error occurred"))
            }
        }
        catch (e: Exception) {
            writeResult.postValue(Result.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}