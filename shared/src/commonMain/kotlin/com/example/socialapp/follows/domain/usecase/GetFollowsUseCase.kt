package com.example.socialapp.follows.domain.usecase

import com.example.socialapp.common.domain.model.FollowsUser
import com.example.socialapp.common.util.Result
import com.example.socialapp.follows.domain.FollowsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class GetFollowsUseCase: KoinComponent {
    private val followsRepository: FollowsRepository by inject()

    suspend operator fun invoke(
        userId: Long,
        page: Int,
        pageSize: Int,
        followsType: Int
    ): Result<List<FollowsUser>> {
        return followsRepository.getFollows(userId, page, pageSize, followsType)
    }
}