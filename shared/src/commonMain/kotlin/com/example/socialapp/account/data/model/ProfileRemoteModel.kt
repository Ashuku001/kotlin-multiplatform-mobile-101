package com.example.socialapp.account.data.model

import com.example.socialapp.account.domain.model.Profile
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteProfile (
    val id: Long,
    val name: String,
    val bio: String,
    val imageUrl: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean,
    val isOwnProfile: Boolean
) {
    fun toDomainProfile(): Profile {
        return Profile(
            id, name, bio, imageUrl, followersCount, followingCount, isFollowing, isOwnProfile
        )
    }
}

@Serializable
internal data class ProfileApiResponseData(
    val success: Boolean,
    val profile: RemoteProfile,
    val message: String?
)

@Serializable
internal data class ProfileApiResponse(
    @Contextual val code: HttpStatusCode,
    val data: ProfileApiResponseData
)

