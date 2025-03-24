package com.example.socialapp.account.data.model

import com.example.socialapp.account.domain.model.Profile
import com.example.socialapp.common.data.local.UserSettings

fun UserSettings.toDomainProfile(): Profile {
    return Profile(id, name, bio, imageUrl = avatar, followersCount, followingCount, isFollowing = false, isOwnProfile = true)
}

fun Profile.toUserSettings(token: String): UserSettings {
    return UserSettings(id, name, bio, avatar = imageUrl, followersCount = followersCount, followingCount = followingCount, token = token)
}