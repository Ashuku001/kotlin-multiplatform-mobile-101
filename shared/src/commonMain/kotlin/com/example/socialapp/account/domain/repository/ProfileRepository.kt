package com.example.socialapp.account.domain.repository

import com.example.socialapp.account.domain.model.Profile
import com.example.socialapp.common.util.Result
import kotlinx.coroutines.flow.Flow

internal interface ProfileRepository {
    // Flow allow async fetch and handle reactively
    fun getProfile(profileId: Long): Flow<Result<Profile>>
}