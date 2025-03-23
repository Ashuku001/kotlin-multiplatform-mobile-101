package com.example.socialapp.auth.data

import com.example.socialapp.common.data.remote.KtorApi
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class AuthService: KtorApi() {
    suspend fun signUp(request: SignUpRequest): AuthResponse = client.post {
        endpoint(path = "signup")
        setBody(request)
    }.body() // call the endpoint with the request get results from body

    suspend fun signIn(request: SignInRequest): AuthResponse = client.post {
        endpoint(path = "signin")
        setBody(request)
    }.body()
}