package com.example.socialapp.post.domain.usecase

import com.example.socialapp.common.util.Result
import com.example.socialapp.post.domain.model.PostComment
import com.example.socialapp.post.domain.repository.PostCommentRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetPostCommentsUseCase: KoinComponent {
    val repository: PostCommentRepository by inject<PostCommentRepository>()

    suspend operator fun invoke(
        postId: Long,
        page: Int,
        pageSize: Int
    ): Result<List<PostComment>> {
        return repository.getPostComments(postId, page, pageSize)
    }
}