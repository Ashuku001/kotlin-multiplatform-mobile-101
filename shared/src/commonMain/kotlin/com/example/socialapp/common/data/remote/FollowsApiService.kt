package com.example.socialapp.common.data.remote

import com.example.socialapp.common.data.model.FollowsApiResponse
import com.example.socialapp.common.data.model.FollowsOrUnfollowAPiResponse
import com.example.socialapp.common.data.model.FollowsParams
import com.example.socialapp.common.util.Constants
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

// used in multiple screens
internal class FollowsApiService: KtorApi() {
    suspend fun followUser(
        userToken: String,
        followsParams: FollowsParams
    ): FollowsOrUnfollowAPiResponse {
        val httpResponse = client.post{
            endpoint(path = "/follows/follow")
            setBody(followsParams)
            setToken(userToken)
        }

        return FollowsOrUnfollowAPiResponse(code = httpResponse.status, httpResponse.body())
    }

    suspend fun unfollowUser(
        userToken: String,
        followsParams: FollowsParams
    ): FollowsOrUnfollowAPiResponse {
        val httpResponse = client.post{
            endpoint(path = "/follows/unfollow")
            setBody(followsParams)
            setToken(userToken)
        }

        return FollowsOrUnfollowAPiResponse(code = httpResponse.status, httpResponse.body())
    }

    suspend fun getFollowableUsers(userToken: String, userId: Long): FollowsApiResponse {
        val httpResponse = client.get{
            endpoint(path = "/follows/suggestions")
            parameter(key = Constants.USER_ID_PARAMETERS, value = userId)
            setToken(userToken)
        }

        println("HTTP RESPONSE $httpResponse")

        return FollowsApiResponse(code = httpResponse.status, data = httpResponse.body())
    }

}