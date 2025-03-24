package com.example.socialapp.post.data

import com.example.socialapp.common.data.local.UserPreferences
import com.example.socialapp.common.data.local.UserSettings
import com.example.socialapp.common.data.model.LikeParams
import com.example.socialapp.common.data.model.PostsApiResponse
import com.example.socialapp.common.data.remote.PostApiService
import com.example.socialapp.common.domain.model.Post
import com.example.socialapp.common.util.Constants
import com.example.socialapp.common.util.DispatcherProvider
import com.example.socialapp.common.util.Result
import com.example.socialapp.post.domain.PostRepository
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import okio.IOException

internal class PostRepositoryImpl (
    private val userPreferences: UserPreferences,
    private val dispatcher: DispatcherProvider,
    private val postApiService: PostApiService
) : PostRepository {
    override suspend fun getFeedPosts(page: Int, pageSize: Int): Result<List<Post>> {
        // a bg thread
        return fetchPosts {currentUserData ->
            postApiService.getFeedPosts(
                userToken = currentUserData.token,
                currentUserId = currentUserData.id,
                page = page,
                pageSize = pageSize
            )
        }
    }

    override suspend fun likeOrUnlikePost(postId: Long, shouldLike: Boolean): Result<Boolean> {
        return withContext(dispatcher.io) {
            try {
                val userData = userPreferences.getUserData()
                val apiResponse = if(shouldLike) {
                    postApiService.likePost(userToken = userData.token, likeParams = LikeParams(postId = postId, userId = userData.id))
                } else {
                    postApiService.unlikePost(userToken = userData.token, likeParams = LikeParams(postId = postId, userId = userData.id))
                }
                when(apiResponse.code) {
                    HttpStatusCode.OK -> {
                        Result.Success(data = apiResponse.data.success)
                    }
                    else -> {
                        Result.Error(
                            data = false,
                            message = "${apiResponse.data.message}"
                        )
                    }
                }
            } catch (ioException: IOException){
                Result.Error(message = Constants.NO_INTERNET_ERROR)
            } catch (anyError: Throwable) {
                println("exception $anyError")
                Result.Error(message = "${anyError.message}")
            }
        }
    }

    override suspend fun getUserPosts(userId: Long, page: Int, pageSize: Int): Result<List<Post>>  {
        return fetchPosts(
            apiCall = {currentUserData ->
                postApiService.getUserPosts(
                    token = currentUserData.token,
                    userId = userId,
                    currentUserId = currentUserData.id,
                    page = page,
                    pageSize = pageSize
                )
            }
        )
    }

    // abstract the common methods of fetching posts
    private suspend fun fetchPosts(
        apiCall: suspend (UserSettings) -> PostsApiResponse // api call with current user data and return the respose
    ): Result<List<Post>> {
        return withContext(dispatcher.io){
            try {
                val currentUserData = userPreferences.getUserData()
                val apiResponse = apiCall(currentUserData)

                when (apiResponse.code) {
                    HttpStatusCode.OK -> {
                        Result.Success(data = apiResponse.data.posts.map { it.toDomainPost() })
                    }
                    else -> {
                        Result.Error(
                            message = Constants.UNEXPECTED_ERROR
                        )
                    }
                }
            } catch (ioException: IOException) {
                Result.Error(
                    message = Constants.NO_INTERNET_ERROR
                )
            } catch (exception: Throwable) {
                Result.Error(
                    message = "${exception.cause}"
                )
            }
        }
    }
}