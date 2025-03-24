package com.example.socialapp.di

import com.example.socialapp.account.AccountApiService
import com.example.socialapp.account.data.repository.ProfileRepositoryImpl
import com.example.socialapp.account.domain.repository.ProfileRepository
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
import com.example.socialapp.post.data.PostRepositoryImpl
import com.example.socialapp.post.domain.PostRepository
import com.example.socialapp.post.domain.usecase.GetPostsUseCase
import com.example.socialapp.post.domain.usecase.GetUserPostsUseCase
import com.example.socialapp.post.domain.usecase.LikeOrUnlikePostUseCase
import org.koin.dsl.module

private val authModule = module{
    // 2 gets for dispatcher and authService modules
    factory  {AuthService()} // construct an instance each time it is needed
    factory {SignUpUseCase()}
    factory {SignInUseCase()}

    single<AuthRepository> {AuthRepositoryImpl(get(), get(), get())} // a single instance of auth repo

}

private val utilityModule = module {
    factory { provideDispatcher() }
}

private val postModule = module {
    factory {PostApiService()}
    factory { GetPostsUseCase() }
    factory { LikeOrUnlikePostUseCase() }
    factory { GetUserPostsUseCase() }

    single<PostRepository> {PostRepositoryImpl(get(), get(), get())}
}

private val followsModule = module {
    factory {FollowsApiService()}
    factory {GetFollowableUsersUseCase()}
    factory {FollowOrUnfollowUseCase()}

    single<FollowsRepository>  {FollowsRepositoryImpl(get(), get(), get())}
}

private val accountModule = module {
    factory {AccountApiService()}
    factory {GetPostsUseCase()}

    single<ProfileRepository> {ProfileRepositoryImpl(get(), get(), get())}
}

// inject to koin
fun getSharedModules() = listOf(platformModule, authModule, utilityModule, postModule, followsModule, accountModule)