package com.rpl.fintrack.database.remote.retrofit

import com.rpl.fintrack.database.remote.request.WriteTransactionRequest
import com.rpl.fintrack.database.remote.response.MessageTransactionResponse
import com.rpl.fintrack.database.remote.response.MonthlySummaryResponse
import com.rpl.fintrack.database.remote.response.TransactionsListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TransactionApiService {
    @POST("create")
    suspend fun writeTransaction(
        @Body request: WriteTransactionRequest
    ) : Response<MessageTransactionResponse>

    @GET("list")
    suspend fun transactionsList(
        @Query("uid") uid: String,
        @Query("date") date: String
    ) : Response<TransactionsListResponse>

    @GET("summary")
    suspend fun transactionsSummary(
        @Query("year") year: Int,
        @Query("month") month: Int
    ) : Response<MonthlySummaryResponse>

    @DELETE("delete")
    suspend fun deleteTransaction(
        @Query("tid") tid: String
    ) : Response<MessageTransactionResponse>
}