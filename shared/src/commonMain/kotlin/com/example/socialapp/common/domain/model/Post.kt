package com.example.socialapp.common.domain.model

data class Post(
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
)
