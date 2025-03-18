package com.example.socialapp.auth.data

// all data set and received from our server

import kotlinx.serialization.Serializable

// make the classes serializable meaning it can be converted to and from formats like JSON, ProtoBuf, or CBOR. i.e., a format that can be stored or transmitted
@Serializable
internal data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
internal data class SignInRequest(
    val email: String,
    val password: String
)

@Serializable
internal data class AuthResponse(
    val data: AuthResponseData?= null,
    val errorMessage: String? = null
)

@Serializable
internal data class AuthResponseData(
    val id: Int,
    val name: String,
    val email: String,
    val bio: String,
    val avatar: String? = null,
    val token: String,
    val followersCount: Int = 0,
    val followingCount: Int = 0
)