package com.rpl.fintrack.database.remote.request

data class LoginRequest(
	val password: String? = null,
	val email: String? = null
)

