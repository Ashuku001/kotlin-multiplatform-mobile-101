package com.example.socialapp.android.di

import com.example.socialapp.android.auth.login.LoginViewModal
import com.example.socialapp.android.auth.signup.SignupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {LoginViewModal()}
    viewModel {SignupViewModel()}
}