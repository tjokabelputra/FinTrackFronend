package com.rpl.fintrack.database.remote.request

data class RegisterRequest(
	val password: String? = null,
	val email: String? = null,
	val username: String? = null
)

