package com.rpl.fintrack.database.remote.response

import com.google.gson.annotations.SerializedName

data class MonthlySummaryResponse(

	@field:SerializedName("income")
	val income: List<IncomeItem?>? = null,

	@field:SerializedName("total")
	val total: List<TotalItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("expenses")
	val expenses: List<ExpensesItem?>? = null
)

data class ExpensesItem(

	@field:SerializedName("total_amount")
	val totalAmount: Int? = null,

	@field:SerializedName("category")
	val category: String? = null
)

data class IncomeItem(

	@field:SerializedName("total_amount")
	val totalAmount: Int? = null,

	@field:SerializedName("category")
	val category: String? = null
)

data class TotalItem(

	@field:SerializedName("total_type")
	val totalType: Int? = null,

	@field:SerializedName("type")
	val type: String? = null
)
