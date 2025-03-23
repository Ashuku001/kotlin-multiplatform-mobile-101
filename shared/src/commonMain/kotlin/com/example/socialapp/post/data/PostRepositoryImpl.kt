package com.example.socialapp.post.data

import com.example.socialapp.common.data.local.UserPreferences
import com.example.socialapp.common.data.model.LikeParams
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
        return withContext(dispatcher.io){
            try {
                val userData = userPreferences.getUserData()
                val apiResponse = postApiService.getFeedPosts(
                    userToken = userData.token,
                    currentUserId = userData.id,
                    page = page,
                    pageSize = pageSize
                )

                when (apiResponse.code) {
                    HttpStatusCode.OK -> {
                        Result.Success(
                            data = apiResponse.data.posts.map {
                                it.toDomainPost()
                            }
                        )
                    }

                    HttpStatusCode.BadRequest -> {
                        Result.Error(message = "Error: ${apiResponse.data.message}")
                    }

                    else -> {
                        Result.Error(message = Constants.UNEXPECTED_ERROR)
                    }
                }

            } catch (ioException: IOException){
                Result.Error(message = Constants.NO_INTERNET_ERROR)
            } catch (anyError: Throwable) {
                Result.Error(message = "${anyError.message}")
            }
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
}