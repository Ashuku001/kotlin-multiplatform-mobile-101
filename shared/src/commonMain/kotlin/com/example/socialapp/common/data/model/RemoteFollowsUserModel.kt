package com.example.socialapp.common.data.model

import com.example.socialapp.common.domain.model.FollowsUser
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

@Serializable
internal data class FollowsParams(
    val follower: Long,
    val following: Long
)

@Serializable
internal data class RemoteFollowUser(
    val id: Long,
    val name: String,
    val bio: String,
    val imageUrl: String? = null,
    val isFollowing: Boolean
) {
    // a method to convert input into this object
    fun toDomainFollowUser(): FollowsUser {
        return FollowsUser(id, name, bio, imageUrl, isFollowing)
    }
}

@Serializable
internal data class FollowsApiResponseData(
    val success: Boolean,
    val follows: List<RemoteFollowUser> = listOf(),
    val message: String? = null
)

internal data class FollowsApiResponse(
    val code: HttpStatusCode,
    val data: FollowsApiResponseData
)

@Serializable
internal data class FollowsOrUnfollowApiResponseData(
    val success: Boolean,
    val message: String? = null
)

internal data class FollowsOrUnfollowAPiResponse(
    val code: HttpStatusCode,
    val data: FollowsOrUnfollowApiResponseData
)