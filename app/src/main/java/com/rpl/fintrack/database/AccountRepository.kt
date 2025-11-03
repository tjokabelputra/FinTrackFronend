package com.rpl.fintrack.database

import com.rpl.fintrack.database.remote.request.LoginRequest
import com.rpl.fintrack.database.remote.request.RegisterRequest
import com.rpl.fintrack.database.remote.retrofit.AccountApiService

class AccountRepository private constructor(
    private val accountApiService: AccountApiService
){
    suspend fun createAccount(request: RegisterRequest) = accountApiService.createAccount(request)

    suspend fun loginAccount(request: LoginRequest) = accountApiService.loginAccount(request)

    companion object{
        @Volatile
        private var instance: AccountRepository? = null
        fun getInstance(
            accountApiService: AccountApiService
        ): AccountRepository =
            instance ?: synchronized(this) {
                instance ?: AccountRepository(accountApiService)
            }.also { instance = it }
    }
}