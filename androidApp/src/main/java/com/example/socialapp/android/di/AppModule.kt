package com.example.socialapp.android.di

import com.example.socialapp.android.auth.login.LoginViewModel
import com.example.socialapp.android.auth.signup.SignupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// dependency injection using Koin
val appModule = module {
    viewModel {LoginViewModel(get())} // register LoginViewModel on calling koinViewModel() in a composable we receive an instance of LoginViewModel
    viewModel {SignupViewModel(get())}
}