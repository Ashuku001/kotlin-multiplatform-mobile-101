package com.example.socialapp.account

import com.example.socialapp.account.data.model.ProfileApiResponse
import com.example.socialapp.common.data.remote.KtorApi
import com.example.socialapp.common.util.Constants
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class AccountApiService: KtorApi() {
    suspend fun getProfile(
        token: String,
        profileId: Long,
        currentUserId: Long
    ): ProfileApiResponse {
        val httpResponse = client.get{
            endpoint("/profile/$profileId")
            parameter(key = Constants.CURRENT_USER_ID_PARAMETER, value = currentUserId)
            setToken(token = token)
        }

        println("HTTP STATUS CODE $httpResponse")

        return ProfileApiResponse(code = httpResponse.status, data = httpResponse.body())
    }
}