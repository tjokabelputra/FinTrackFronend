package com.rpl.fintrack.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rpl.fintrack.database.AccountRepository
import com.rpl.fintrack.database.remote.response.LoginResponse
import com.rpl.fintrack.database.Result
import com.rpl.fintrack.database.remote.request.LoginRequest
import com.rpl.fintrack.database.remote.response.ErrorResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val accountRepository: AccountRepository) : ViewModel() {
    val loginResult = MutableLiveData<Result<LoginResponse>>()

    fun loginAccount(email: String, password: String) = viewModelScope.launch{
        loginResult.postValue(Result.Loading)
        try{
            val request = LoginRequest(email = email, password = password)
            val response = accountRepository.loginAccount(request)
            when {
                response.isSuccessful -> {
                    val contentResponse = response.body()
                    if(contentResponse != null){
                        loginResult.postValue(Result.Success(contentResponse))
                    }
                    else{
                        loginResult.postValue(Result.Error("Empty Response Body"))
                    }
                }
                else -> {
                    val errorBodyString = response.errorBody()?.string()
                    if (errorBodyString != null) {
                        val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                        loginResult.postValue(Result.Error(errorResponse.message ?: "Error parsing response"))
                    }
                    else {
                        loginResult.postValue(Result.Error("Unknown Error"))
                    }
                }
            }
        }
        catch (e: HttpException) {
            val errorBodyString = e.response()?.errorBody()?.string()
            if (errorBodyString != null) {
                val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                loginResult.postValue(Result.Error(errorResponse.message ?: "Error parsing response"))
            }
            else {
                loginResult.postValue(Result.Error(e.message ?: "An unexpected error occurred"))
            }
        }
        catch (e: Exception) {
            loginResult.postValue(Result.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}