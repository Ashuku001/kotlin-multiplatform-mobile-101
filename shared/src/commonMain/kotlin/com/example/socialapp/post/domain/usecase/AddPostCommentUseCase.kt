package com.example.socialapp.post.domain.usecase

import com.example.socialapp.common.util.Result
import com.example.socialapp.post.domain.model.PostComment
import com.example.socialapp.post.domain.repository.PostCommentRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddPostCommentUseCase: KoinComponent {
    private val repository by inject<PostCommentRepository>()

    suspend operator fun invoke(postId: Long, content: String): Result<PostComment> {
        if (content.isBlank()) {
            return Result.Error(message = "Comment content cannot be blank")
        }

        return repository.addComment(postId = postId, content = content)
    }
}