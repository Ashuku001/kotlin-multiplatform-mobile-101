package com.example.socialapp.android.common.fakedata

import com.example.socialapp.common.domain.model.FollowsUser


data class SampleFollowsUser(
    val id: Int,
    val name: String,
    val bio: String = "Hey there, welcome to my profile",
    val profileUrl: String,
    val isFollowing: Boolean = false
) {
    // a mapper to domain models
    fun toFollowsUser(): FollowsUser {
        return FollowsUser(
            id = id.toLong(),
            name = name,
            bio = bio,
            imageUrl = profileUrl,
            isFollowing = isFollowing
        )
    }
}

val sampleUsers = listOf(
    SampleFollowsUser(
        id = 1,
        name = "Mr Ezra",
        profileUrl = "https://picsum.photos/200"
    ),
    SampleFollowsUser(
        id = 2,
        name = "John Cena",
        profileUrl = "https://picsum.photos/200"
    ),
    SampleFollowsUser(
        id = 3,
        name = "Cristiano",
        profileUrl = "https://picsum.photos/200"
    ),
    SampleFollowsUser(
        id = 4,
        name = "L. James",
        profileUrl = "https://picsum.photos/200"
    )
)