package com.example.socialapp.post.data.repository

import com.example.socialapp.common.data.local.UserPreferences
import com.example.socialapp.common.data.local.UserSettings
import com.example.socialapp.common.data.model.LikeParams
import com.example.socialapp.common.data.model.NewPostParams
import com.example.socialapp.common.data.model.PostsApiResponse
import com.example.socialapp.common.data.remote.PostApiService
import com.example.socialapp.common.domain.model.Post
import com.example.socialapp.common.util.Constants
import com.example.socialapp.common.util.DispatcherProvider
import com.example.socialapp.common.util.Result
import com.example.socialapp.common.util.safeApiCall
import com.example.socialapp.post.domain.repository.PostRepository
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import okio.IOException
import kotlinx.serialization.json.Json


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

    override suspend fun getPost(postId: Long): Result<Post> {
        return withContext(dispatcher.io) {
            try {
                val currentUserData = userPreferences.getUserData()
                val apiResponse = postApiService.getPost(token = currentUserData.token, postId = postId, currentUserId = currentUserData.id)

                when (apiResponse.code) {
                    HttpStatusCode.OK -> {
                        Result.Success(data = apiResponse.data.post!!.toDomainPost())
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

    override suspend fun addPost(
        userId: Long,
        imageBytes: ByteArray,
        caption: String
    ): Result<Post> {
        return safeApiCall (dispatcher) {
            val currentUserData = userPreferences.getUserData()

            val postData = Json.encodeToString(
                serializer = NewPostParams.serializer(),
                value = NewPostParams(
                    userId = userId,
                    caption = caption
                )
            )

            val apiResponse = postApiService.createPost(
                postData = postData,
                token = currentUserData.token,
                imageBytes = imageBytes
            )

            when(apiResponse.code) {
                HttpStatusCode.OK -> {
                    Result.Success(data = apiResponse.data.post!!.toDomainPost())
                }
                else -> {
                    Result.Error(message = "APIError ${apiResponse.data.message}")
                }
            }
        }
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

