package com.example.socialapp.account.data.repository

import com.example.socialapp.account.AccountApiService
import com.example.socialapp.account.data.model.UpdateUserParams
import com.example.socialapp.account.data.model.toDomainProfile
import com.example.socialapp.account.data.model.toUserSettings
import com.example.socialapp.account.domain.model.Profile
import com.example.socialapp.account.domain.repository.ProfileRepository
import com.example.socialapp.common.data.local.UserPreferences
import com.example.socialapp.common.util.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.socialapp.common.util.Result
import com.example.socialapp.common.util.safeApiCall
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json

internal class ProfileRepositoryImpl(
    private val accountApiService: AccountApiService,
    private val preferences: UserPreferences,
    private val dispatcher: DispatcherProvider
): ProfileRepository {
    override fun getProfile(profileId: Long): Flow<Result<Profile>> {
        // a flow builder
        return flow {
            val userData = preferences.getUserData()

            // user accessing their own profile s
            if(profileId == userData.id) {
                emit(Result.Success(userData.toDomainProfile()))
            }

            val apiResponse = accountApiService.getProfile(
                token = userData.token,
                profileId = profileId,
                currentUserId = userData.id
            )

            when (apiResponse.code) {
                HttpStatusCode.OK -> {
                    val profile = apiResponse.data.profile!!.toDomainProfile()

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

    override suspend fun updateProfile(profile: Profile, imageBytes: ByteArray?): Result<Profile> {
        return safeApiCall(dispatcher) {
            val currentUserData = preferences.getUserData()


            val profileData = Json.encodeToString(
                serializer = UpdateUserParams.serializer(),
                value = UpdateUserParams(
                    userId = profile.id,
                    name = profile.name,
                    bio = profile.bio,
                    imageUrl = profile.imageUrl
                )
            )

            val apiResponse = accountApiService.updateProfile(
                profileData = profileData,
                imageBytes = imageBytes,
                token = currentUserData.token
            )

            when(apiResponse.code) {
                HttpStatusCode.OK -> {
                    var imageUrl = profile.imageUrl
                    if (imageBytes != null) {
                        //TODO make the backed to return the profile instead of calling it again
                        val updatedProfileApiResponse = accountApiService.getProfile(
                            token = currentUserData.token,
                            profileId = profile.id,
                            currentUserId = profile.id
                        )

                        updatedProfileApiResponse.data.profile.let {
                            imageUrl = it?.imageUrl
                        }
                    }

                    val updateProfile = profile.copy(imageUrl = imageUrl)
                    preferences.setUserData(updateProfile.toUserSettings(currentUserData.token))

                    Result.Success(data = updateProfile)
                }
                else -> {
                    Result.Error(message = "APIError: ${apiResponse.data.message}")
                }
            }

        }
    }
}