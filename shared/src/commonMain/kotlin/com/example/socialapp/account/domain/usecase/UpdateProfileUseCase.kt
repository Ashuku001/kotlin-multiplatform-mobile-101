package com.example.socialapp.account.domain.usecase

import com.example.socialapp.account.domain.model.Profile
import com.example.socialapp.account.domain.repository.ProfileRepository
import com.example.socialapp.common.util.Constants
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.example.socialapp.common.util.Result

class UpdateProfileUseCase: KoinComponent {
    private val profileRepository: ProfileRepository by inject()

    suspend operator fun invoke(profile: Profile, imageBytes: ByteArray?): Result<Profile> {
        // validate the fields
        with(profile.name){
            if (isBlank() || length < 3 || length > 20){
                return Result.Error(message = Constants.INVALID_INPUT_NAME_MESSAGE)
            }
        }
        with(profile.bio){
            if (isBlank() || length > 150){
                return Result.Error(message = Constants.INVALID_INPUT_BIO_MESSAGE)
            }
        }
        return profileRepository.updateProfile(profile, imageBytes)
    }
}