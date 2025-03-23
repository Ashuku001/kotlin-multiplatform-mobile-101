package com.example.socialapp.auth.data

import com.example.socialapp.auth.domain.model.AuthResultData

// convert the data from server to
internal fun AuthResponseData.toAuthResultData(): AuthResultData {
    return AuthResultData(id, name, email, bio, imageUrl, token, followersCount, followingCount)
}