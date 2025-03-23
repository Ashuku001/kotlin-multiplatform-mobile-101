package com.example.socialapp.auth.domain.model

// a data class to map JSON results to a data class
data class AuthResultData (
    val id: Long,
    val name: String,
    val email: String,
    val bio: String,
    val avatar: String? = null,
    val token: String,
    val followersCount: Int = 0,
    val followingCount: Int = 0
)