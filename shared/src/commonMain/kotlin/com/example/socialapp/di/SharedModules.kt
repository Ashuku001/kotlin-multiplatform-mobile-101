package com.example.socialapp.di

import com.example.socialapp.auth.data.AuthRepositoryImpl
import com.example.socialapp.auth.data.AuthService
import com.example.socialapp.auth.domain.repository.AuthRepository
import com.example.socialapp.auth.domain.usecase.SignInUseCase
import com.example.socialapp.auth.domain.usecase.SignUpUseCase
import com.example.socialapp.common.util.provideDispatcher
import org.koin.dsl.module

private val authModule = module{
    // 2 gets for dispather and authservice modules
    single<AuthRepository> {AuthRepositoryImpl(get(), get(), get())} // a single instance of auth repo
    factory  {AuthService()} // construct an instance each time it is needed
    factory {SignUpUseCase()}
    factory {SignInUseCase()}
}

private val utilityModule = module {
    factory { provideDispatcher() }
}


fun getSharedModules() = listOf(platformModule, authModule, utilityModule)