package com.rpl.fintrack.database.remote.response

import com.google.gson.annotations.SerializedName

data class TransactionsListResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("transactions")
	val transactions: List<TransactionsItem?>? = null
)

data class TransactionsItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("uid")
	val uid: String? = null,

	@field:SerializedName("amount")
	val amount: Float? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("tid")
	val tid: Int? = null
)
