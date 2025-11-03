package com.rpl.fintrack.database.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("account")
	val account: Account? = null,

	@field:SerializedName("token")
	val token: String? = null
)

data class Account(

	@field:SerializedName("uid")
	val uid: String? = null,

	@field:SerializedName("password_hash")
	val passwordHash: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
