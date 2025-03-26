package com.example.socialapp.follows.data

import com.example.socialapp.common.data.local.UserPreferences
import com.example.socialapp.common.data.model.FollowsParams
import com.example.socialapp.common.data.remote.FollowsApiService
import com.example.socialapp.common.domain.model.FollowsUser
import com.example.socialapp.common.util.Constants
import com.example.socialapp.common.util.DispatcherProvider
import com.example.socialapp.follows.domain.FollowsRepository
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import com.example.socialapp.common.util.Result
import com.example.socialapp.common.util.safeApiCall
import okio.IOException

internal class FollowsRepositoryImpl (
    private val followsApiService: FollowsApiService,
    private val userPreferences: UserPreferences,
    private val dispatcher: DispatcherProvider
) : FollowsRepository {
    override suspend fun getFollowableUsers(): Result<List<FollowsUser>> {
        // switch to bg thread
        return withContext(dispatcher.io) {
            try {
                val userData = userPreferences.getUserData()

                // get suggestion if available
                val apiResponse = followsApiService.getFollowableUsers(userData.token, userData.id)

                when (apiResponse.code) {
                    HttpStatusCode.OK -> {
                        Result.Success(
                            data = apiResponse.data.follows.map { it.toDomainFollowUser() }
                        )
                    }

                    HttpStatusCode.BadRequest -> {
                        // Create a seed class to map custom messages depending on the status
                        Result.Error(message = "${apiResponse.data.message}")
                    }

                    HttpStatusCode.Forbidden -> {
                        Result.Success(data = emptyList())
                    }

                    else -> {
                        Result.Error(message = "${apiResponse.data.message}")
                    }
                }
            } catch (ioException: IOException){
                Result.Error(message = Constants.NO_INTERNET_ERROR)
            } catch (anyError: Throwable) {
                Result.Error(message = "Something went wrong.")
            }
        }
    }

    override suspend fun followOrUnfollow(
        followedUserId: Long,
        shouldFollow: Boolean
    ): Result<Boolean> {
        return withContext(dispatcher.io) {
            try {
                val userData = userPreferences.getUserData()
                val followsParams = FollowsParams(follower = userData.id, following = followedUserId)

                val apiResponse = if (shouldFollow) {
                    followsApiService.followUser(userData.token, followsParams)
                } else {
                    followsApiService.unfollowUser(userToken = userData.token, followsParams)
                }

                if (apiResponse.code == HttpStatusCode.OK) {
                    Result.Success(data = apiResponse.data.success)
                } else {
                    Result.Error(
                        data = false,
                        message = "${apiResponse.data.message}"
                    )
                }
            } catch (ioException: IOException){
                Result.Error(message = Constants.NO_INTERNET_ERROR)
            } catch (exception: Throwable) {
                Result.Error(message = "${exception.message}")
            }
        }
    }

    override suspend fun getFollows(
        userId: Long,
        page: Int,
        pageSize: Int,
        followType: Int
    ): Result<List<FollowsUser>> {
        // TODO ("update the code to use this wrapper for safe switching to bg thread")
        return safeApiCall (dispatcher){
            withContext(dispatcher.io){
                val currentUserData = userPreferences.getUserData()
                val apiResponse = followsApiService.getFollows(
                    userToken = currentUserData.token,
                    userId = userId,
                    page = page,
                    pageSize = pageSize,
                    followsEndPoint = if (followType == 1) "followers" else "following"
                )


                if (apiResponse.code == HttpStatusCode.OK){
                    Result.Success(data = apiResponse.data.follows.map { it.toDomainFollowUser() })
                }else{
                    Result.Error(message = "${apiResponse.data.message}")
                }
            }
        }
    }
}