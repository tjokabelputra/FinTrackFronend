package com.rpl.fintrack.database.remote.retrofit

import com.rpl.fintrack.database.remote.request.LoginRequest
import com.rpl.fintrack.database.remote.request.RegisterRequest
import com.rpl.fintrack.database.remote.response.LoginResponse
import com.rpl.fintrack.database.remote.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApiService {
    @POST("create")
    suspend fun createAccount(
        @Body request: RegisterRequest
    ) : Response<RegisterResponse>

    @POST("login")
    suspend fun loginAccount(
        @Body request: LoginRequest
    ) : Response<LoginResponse>
}