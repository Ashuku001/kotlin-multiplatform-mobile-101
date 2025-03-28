package com.example.socialapp.post.domain.usecase

import com.example.socialapp.common.util.Result
import com.example.socialapp.post.domain.model.PostComment
import com.example.socialapp.post.domain.repository.PostCommentRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RemovePostCommentUseCase: KoinComponent {
    private val repository by inject<PostCommentRepository>()

    suspend operator fun invoke(postId: Long, commentId: Long): Result<PostComment?> {
        return repository.removeComment(commentId = commentId, postId = postId)
    }
}