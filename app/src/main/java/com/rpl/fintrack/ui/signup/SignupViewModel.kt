package com.rpl.fintrack.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rpl.fintrack.database.AccountRepository
import com.rpl.fintrack.database.remote.response.RegisterResponse
import com.rpl.fintrack.database.Result
import com.rpl.fintrack.database.remote.request.RegisterRequest
import com.rpl.fintrack.database.remote.response.ErrorResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel(private val accountRepository: AccountRepository) : ViewModel() {
    val registerResult = MutableLiveData<Result<RegisterResponse>>()

    fun registerAccount(username: String, email: String, password: String) = viewModelScope.launch {
        registerResult.postValue(Result.Loading)
        try{
            val request = RegisterRequest(username = username, email = email, password = password)
            val response = accountRepository.createAccount(request)
            when {
                response.isSuccessful -> {
                    val contentResponse = response.body()
                    if(contentResponse != null){
                        registerResult.postValue(Result.Success(contentResponse))
                    }
                    else{
                        registerResult.postValue(Result.Error("Empty Response Body"))
                    }
                }
                else -> {
                    val errorBodyString = response.errorBody()?.string()
                    if (errorBodyString != null) {
                        val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                        registerResult.postValue(Result.Error(errorResponse.message ?: "Error parsing response"))
                    }
                    else {
                        registerResult.postValue(Result.Error("Unknown Error"))
                    }
                }
            }
        }
        catch (e: HttpException) {
            val errorBodyString = e.response()?.errorBody()?.string()
            if (errorBodyString != null) {
                val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                registerResult.postValue(Result.Error(errorResponse.message ?: "Error parsing response"))
            }
            else {
                registerResult.postValue(Result.Error(e.message ?: "An unexpected error occurred"))
            }
        }
        catch (e: Exception) {
            registerResult.postValue(Result.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}