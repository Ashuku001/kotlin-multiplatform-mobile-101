package com.example.socialapp.account.domain.usecase

import com.example.socialapp.account.domain.model.Profile
import com.example.socialapp.account.domain.repository.ProfileRepository
import com.example.socialapp.common.util.Result
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetProfileUseCase: KoinComponent {
    // KoinComponent extension to allow injecting of the Repository
    private val repository: ProfileRepository by inject()

    // allow calling of GetProfileUseCase as if it was a function
    operator fun invoke(profileId: Long): Flow<Result<Profile>> {
        return repository.getProfile(profileId = profileId)
    }
}