package com.rpl.fintrack.database.remote.request

import com.google.gson.annotations.SerializedName

data class WriteTransactionRequest(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("uid")
	val uid: String? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("category")
	val category: String? = null
)
