package com.example.socialapp.common.data.model

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import com.example.socialapp.common.domain.model.Post
import com.example.socialapp.common.util.DateFormatter

@Serializable
internal data class RemotePost(
    val postId: Long,
    val caption: String,
    val imageUrl: String,
    val likesCount: Int,
    val commentsCount:Int,
    val userId: Long,
    val userName: String,
    val userImageUrl: String?,
    val createdAt: String,
    val isLiked: Boolean, // says if current user has liked the psot
    val isOwnPost: Boolean, // check if the post belongs to current user
) {
    fun toDomainPost(): Post {
        return Post(
            postId = postId,
            caption = caption,
            imageUrl = imageUrl,
            likesCount = likesCount,
            commentsCount = commentsCount,
            userId = userId,
            userName = userName,
            userImageUrl = userImageUrl,
            createdAt = DateFormatter.parseDate(createdAt),
            isLiked = isLiked,
            isOwnPost = isOwnPost
        )
    }
}


@Serializable
internal data class PostsApiResponseData(
    val success: Boolean,
    val posts: List<RemotePost> = listOf(),
    val message: String? = null,
)

internal data class PostsApiResponse(
    val code: HttpStatusCode,
    val data: PostsApiResponseData
)