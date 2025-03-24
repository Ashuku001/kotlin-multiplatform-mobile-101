package com.example.socialapp.account.data.repository

import com.example.socialapp.account.AccountApiService
import com.example.socialapp.account.data.model.toDomainProfile
import com.example.socialapp.account.data.model.toUserSettings
import com.example.socialapp.account.domain.model.Profile
import com.example.socialapp.account.domain.repository.ProfileRepository
import com.example.socialapp.common.data.local.UserPreferences
import com.example.socialapp.common.util.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import com.example.socialapp.common.util.Result
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

internal class ProfileRepositoryImpl(
    private val accountApiService: AccountApiService,
    private val preferences: UserPreferences,
    private val dispatcher: DispatcherProvider
): ProfileRepository {
    override fun getProfile(profileId: Long): Flow<Result<Profile>> {
        // a flow builder
        return flow {
            println("PROFIEL ID $profileId")
            val userData = preferences.getUserData()

            println("USER DATA $userData")

            // user accessing their own profile s
            if(profileId == userData.id) {
                emit(Result.Success(userData.toDomainProfile()))
            }

            val apiResponse = accountApiService.getProfile(
                token = userData.token,
                profileId = profileId,
                currentUserId = userData.id
            )

            println("API RESPOSE $apiResponse")

            when (apiResponse.code) {
                HttpStatusCode.OK -> {
                    val profile = apiResponse.data.profile.toDomainProfile()

                    // update user shared preference if the profile is the current users profile
                    if (profileId == userData.id) {
                        preferences.setUserData(profile.toUserSettings(token = userData.token))
                    }

                    emit(Result.Success(profile))
                }
                else -> {
                    emit(Result.Error(message = "Error: ${apiResponse.data.message}"))
                }
            }
        }.catch { exception ->
            emit(Result.Error(message = "Error: ${exception.message}"))
        }.flowOn(dispatcher.io) // upstream flow run on dispatcher.io
    }
}