package com.example.socialapp.post.data.repository

import com.example.socialapp.common.data.local.UserPreferences
import com.example.socialapp.common.util.Constants
import com.example.socialapp.common.util.DispatcherProvider
import com.example.socialapp.common.util.Result
import com.example.socialapp.post.data.models.NewCommentParams
import com.example.socialapp.post.data.models.RemoveCommentParams
import com.example.socialapp.post.data.remote.PostCommentApiService
import com.example.socialapp.post.domain.model.PostComment
import com.example.socialapp.post.domain.repository.PostCommentRepository
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import okio.IOException

internal class PostCommentRepositoryImpl(
    private val preference: UserPreferences,
    private val postCommentsApiService: PostCommentApiService,
    private val dispatcherProvider: DispatcherProvider
): PostCommentRepository {
    override suspend fun getPostComments(
        postId: Long,
        page: Int,
        pageSize: Int
    ): Result<List<PostComment>> {
        return withContext(dispatcherProvider.io) {
            try {
                val currentUserData = preference.getUserData()

                val apiResponse = postCommentsApiService.getPostComments(
                    postId = postId,
                    userToken = currentUserData.token,
                    page = page,
                    pageSize = pageSize
                )

                when(apiResponse.code) {
                    HttpStatusCode.OK ->
                        Result.Success(data = apiResponse.data.comments.map { it.toDomainPostComment() })
                    else -> Result.Error(message = apiResponse.data.message ?: Constants.UNEXPECTED_ERROR)
                }
            } catch (ioException: IOException) {
                Result.Error(message = Constants.NO_INTERNET_ERROR)
            } catch (exception: Throwable) {
                Result.Error(
                    message = Constants.UNEXPECTED_ERROR
                )
            }
        }
    }

    override suspend fun addComment(postId: Long, content: String): Result<PostComment> {
        return withContext(dispatcherProvider.io) {
            try {
                val currentUserData = preference.getUserData()
                val params = NewCommentParams(
                    postId = postId,
                    content = content,
                    userId = currentUserData.id
                )

                val apiResponse = postCommentsApiService.addPostComment(
                    userToken = currentUserData.token,
                    commentParams = params
                )
                when(apiResponse.code) {
                    HttpStatusCode.OK ->
                        Result.Success(data = apiResponse.data.comment!!.toDomainPostComment())
                    else -> Result.Error(message = apiResponse.data.message ?: Constants.UNEXPECTED_ERROR)
                }
            } catch (ioException: IOException) {
                Result.Error(message = Constants.NO_INTERNET_ERROR)
            } catch (exception: Throwable) {
                Result.Error(
                    message = Constants.UNEXPECTED_ERROR
                )
            }
        }
    }

    override suspend fun removeComment(postId: Long, commentId: Long): Result<PostComment?> {
        return withContext(dispatcherProvider.io) {
            try {
                val currentUserData = preference.getUserData()
                val params = RemoveCommentParams(
                    postId = postId,
                    commentId= commentId,
                    userId = currentUserData.id
                )

                val apiResponse = postCommentsApiService.removeComment(
                    userToken = currentUserData.token,
                    commentParams = params
                )
                when(apiResponse.code) {
                    HttpStatusCode.OK ->
                        Result.Success(data = apiResponse.data.comment!!.toDomainPostComment())
                    else -> Result.Error(message = apiResponse.data.message ?: Constants.UNEXPECTED_ERROR)
                }
            } catch (ioException: IOException) {
                Result.Error(message = Constants.NO_INTERNET_ERROR)
            } catch (exception: Throwable) {
                Result.Error(
                    message = Constants.UNEXPECTED_ERROR
                )
            }
        }
    }
}