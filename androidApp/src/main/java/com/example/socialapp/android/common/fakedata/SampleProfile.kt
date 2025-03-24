package com.example.socialapp.android.common.fakedata

import com.example.socialapp.account.domain.model.Profile

data class SampleProfile(
    val id: Long,
    val name: String,
    val bio: String,
    val profileUrl: String,
    val followersCount: Int,
    val followingCount: Int,
    val isOwnProfile: Boolean = false,
    val isFollowing: Boolean = false
) {
    fun toDomainProfile (): Profile {
        return Profile(
            id,
            name,
            bio,
            imageUrl=profileUrl,
            followersCount,
            followingCount,
            isFollowing,
            isOwnProfile
        )
    }
}


val sampleProfiles = listOf(
    SampleProfile(
        id = 1,
        name = "Mr Dip",
        bio = "Hey there, welcome to my Social App page!",
        profileUrl = "https://picsum.photos/200",
        followersCount = 23,
        followingCount = 13,
        isOwnProfile = true,
        isFollowing = true
    ),

    SampleProfile(
        id = 2,
        name = "John Cena",
        profileUrl = "https://picsum.photos/200",
        bio = "Hey there, welcome to my Social App page!",
        followersCount = 23,
        followingCount = 13,
        isOwnProfile = true,
        isFollowing = true
    ),

    SampleProfile(
        id = 3,
        name = "Cristiano",
        profileUrl = "https://picsum.photos/200",
        bio = "Hey there, welcome to my Social App page!",
        followersCount = 23,
        followingCount = 13,
        isOwnProfile = true,
        isFollowing = true
    ),

    SampleProfile(
        id = 4,
        name = "L. James",
        profileUrl = "https://picsum.photos/200",
        bio = "Hey there, welcome to my Social App page!",
        followersCount = 23,
        followingCount = 13,
        isOwnProfile = true,
        isFollowing = true
    )
)