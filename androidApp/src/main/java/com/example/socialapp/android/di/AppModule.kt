package com.example.socialapp.android.di

import com.example.socialapp.android.MainActivityViewModel
import com.example.socialapp.android.account.edit.EditProfileViewModel
import com.example.socialapp.android.account.follows.FollowsViewModel
import com.example.socialapp.android.account.profile.ProfileViewModel
import com.example.socialapp.android.auth.login.LoginViewModel
import com.example.socialapp.android.auth.signup.SignupViewModel
import com.example.socialapp.android.common.util.ImageBytesReader
import com.example.socialapp.android.home.HomeScreenViewModel
import com.example.socialapp.android.post.PostDetailScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// dependency injection using Koin
val appModule = module {
    viewModel {LoginViewModel(get(), get())} // register LoginViewModel on calling koinViewModel() in a composable we receive an instance of LoginViewModel firt get for signup use case second for datastore as required by constructor
    viewModel {SignupViewModel(get(), get())}
    viewModel {MainActivityViewModel(get())}
    viewModel {HomeScreenViewModel(get(), get(), get(), get())}
    viewModel {PostDetailScreenViewModel(get(), get(), get(), get(), get())}
    viewModel {ProfileViewModel(get(), get(), get(), get())}
    viewModel {EditProfileViewModel(get(), get(), get())}
    viewModel {FollowsViewModel(get())}
    
    single {ImageBytesReader(androidContext())}
}