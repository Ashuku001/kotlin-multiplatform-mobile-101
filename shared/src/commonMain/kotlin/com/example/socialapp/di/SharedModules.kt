package com.example.socialapp.di

import com.example.socialapp.account.AccountApiService
import com.example.socialapp.account.data.repository.ProfileRepositoryImpl
import com.example.socialapp.account.domain.repository.ProfileRepository
import com.example.socialapp.account.domain.usecase.GetProfileUseCase
import com.example.socialapp.account.domain.usecase.UpdateProfileUseCase
import com.example.socialapp.auth.data.AuthRepositoryImpl
import com.example.socialapp.auth.data.AuthService
import com.example.socialapp.auth.domain.repository.AuthRepository
import com.example.socialapp.auth.domain.usecase.SignInUseCase
import com.example.socialapp.auth.domain.usecase.SignUpUseCase
import com.example.socialapp.common.data.remote.FollowsApiService
import com.example.socialapp.common.data.remote.PostApiService
import com.example.socialapp.common.util.provideDispatcher
import com.example.socialapp.follows.data.FollowsRepositoryImpl
import com.example.socialapp.follows.domain.FollowsRepository
import com.example.socialapp.follows.domain.usecase.FollowOrUnfollowUseCase
import com.example.socialapp.follows.domain.usecase.GetFollowableUsersUseCase
import com.example.socialapp.follows.domain.usecase.GetFollowsUseCase
import com.example.socialapp.post.data.remote.PostCommentApiService
import com.example.socialapp.post.data.repository.PostCommentRepositoryImpl
import com.example.socialapp.post.data.repository.PostRepositoryImpl
import com.example.socialapp.post.domain.repository.PostCommentRepository
import com.example.socialapp.post.domain.repository.PostRepository
import com.example.socialapp.post.domain.usecase.CreatePostUseCase
import com.example.socialapp.post.domain.usecase.AddPostCommentUseCase
import com.example.socialapp.post.domain.usecase.GetPostCommentsUseCase
import com.example.socialapp.post.domain.usecase.GetPostUseCase
import com.example.socialapp.post.domain.usecase.GetPostsUseCase
import com.example.socialapp.post.domain.usecase.GetUserPostsUseCase
import com.example.socialapp.post.domain.usecase.LikeOrUnlikePostUseCase
import com.example.socialapp.post.domain.usecase.RemovePostCommentUseCase
import org.koin.dsl.module

private val authModule = module{
    // 2 gets for dispatcher and authService modules
    factory  {AuthService()} // construct an instance each time it is needed
    factory {SignUpUseCase()}
    factory {SignInUseCase()}

    single<AuthRepository> {AuthRepositoryImpl(get(), get(), get())} // a single instance of for the lifetime

}

private val utilityModule = module {
    factory { provideDispatcher() }
}

private val postModule = module {
    factory { PostApiService() }
    factory { GetPostsUseCase() }
    factory { LikeOrUnlikePostUseCase() }
    factory { GetUserPostsUseCase() }
    factory { GetPostUseCase() }
    factory { CreatePostUseCase() }

    single<PostRepository> { PostRepositoryImpl(get(), get(), get()) }
}

private val followsModule = module {
    factory { FollowsApiService() }
    factory { GetFollowableUsersUseCase() }
    factory { FollowOrUnfollowUseCase() }
    factory { GetFollowsUseCase() }

    single<FollowsRepository>  {FollowsRepositoryImpl(get(), get(), get())}
}

private val accountModule = module {
    factory { AccountApiService() }
    factory { GetPostsUseCase() }
    factory { GetProfileUseCase() }
    factory { UpdateProfileUseCase() }

    single<ProfileRepository> {ProfileRepositoryImpl(get(), get(), get())}
}

private val postCommentModule  = module {
    factory { PostCommentApiService() }
    factory { GetPostCommentsUseCase() }
    factory { AddPostCommentUseCase() }
    factory { RemovePostCommentUseCase() }

    single<PostCommentRepository> {PostCommentRepositoryImpl(get(), get(), get())}
}

// inject to koin
fun getSharedModules() = listOf(platformModule, authModule, utilityModule, postModule, followsModule, accountModule, postCommentModule)