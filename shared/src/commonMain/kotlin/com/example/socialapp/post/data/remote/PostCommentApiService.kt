package com.example.socialapp.post.data.remote

import com.example.socialapp.common.data.remote.KtorApi
import com.example.socialapp.common.util.Constants
import com.example.socialapp.post.data.models.CommentApiResponse
import com.example.socialapp.post.data.models.GetPostCommentsApiResponse
import com.example.socialapp.post.data.models.NewCommentParams
import com.example.socialapp.post.data.models.RemoveCommentParams
import com.example.socialapp.post.domain.model.PostComment
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class PostCommentApiService: KtorApi() {
    suspend fun getPostComments(
        userToken: String,
        postId: Long,
        page: Int,
        pageSize: Int
    ): GetPostCommentsApiResponse {
        val httpResponse = client.get{
            endpoint("/post/comments/$postId")
            parameter(key = Constants.PAGE_QUERY_PARAMETER, page)
            parameter(key = Constants.PAGE_SIZE_QUERY_PARAMETER, pageSize)
            setToken(token=userToken)
        }

        return GetPostCommentsApiResponse(code = httpResponse.status, data = httpResponse.body())
    }

    suspend fun addPostComment(
        userToken: String,
        commentParams: NewCommentParams
    ): CommentApiResponse {
        val httpResponse = client.post{
            endpoint("/post/comments/create")
            setBody(body = commentParams)
            setToken(token = userToken)
        }

        return CommentApiResponse(code = httpResponse.status, data = httpResponse.body())
    }

    suspend fun removeComment(
        userToken: String,
        commentParams: RemoveCommentParams
    ): CommentApiResponse {
        val httpResponse = client.delete{
            endpoint("/post/comments/delete")
            setBody(commentParams)
            setToken(userToken)
        }

        return CommentApiResponse(code = httpResponse.status, data = httpResponse.body())
    }
}