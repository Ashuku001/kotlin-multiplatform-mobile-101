package com.example.socialapp.post.domain.usecase

import com.example.socialapp.common.domain.model.Post
import com.example.socialapp.common.util.Result
import com.example.socialapp.post.domain.repository.PostRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddNewPostUseCase: KoinComponent {
    val repository by inject<PostRepository>()

    suspend operator fun invoke(
        userId: Long,
        imageBytes: ByteArray,
        caption: String
    ): Result<Post> {
        return repository.addPost(
            userId = userId,
            caption = caption,
            imageBytes = imageBytes
        )
    }
}