package com.example.socialapp.post.data.models

import com.example.socialapp.post.domain.model.PostComment
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
internal data class RemotePostComment(
    val commentId: Long,
    val content: String,
    val postId: Long,
    val userId: Long,
    val userName: String,
    val userImageUrl: String?,
    val createdAt: String
) {
    fun toDomainPostComment (isOwner: Boolean): PostComment {
        return PostComment(
            postId,
            content,
            postId,
            userId,
            userName,
            userImageUrl,
            createdAt,
            isOwner = isOwner
        )
    }
}

@Serializable
internal data class GetCommentsResponseData(
    val success: Boolean,
    val comments: List<RemotePostComment> = listOf(),
    val message: String? = null
)

@Serializable
internal class GetPostCommentsApiResponse(
    @Contextual val code: HttpStatusCode,
    val data: GetCommentsResponseData
)

@Serializable
internal data class CommentResponse(
    val success: Boolean,
    val comment: RemotePostComment? = null,
    val message: String? = null
)

@Serializable
internal class CommentApiResponse(
    @Contextual val code: HttpStatusCode,
    val data: CommentResponse
)

@Serializable
internal data class NewCommentParams(
    val content: String,
    val postId: Long,
    val userId: Long
)

@Serializable
data class RemoveCommentParams(
    val postId: Long,
    val commentId: Long,
    val userId: Long
)

