package com.example.socialapp.android.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.socialapp.android.MainActivityViewModel
import com.example.socialapp.android.account.edit.EditProfileViewModel
import com.example.socialapp.android.account.follows.FollowsViewModel
import com.example.socialapp.android.account.profile.ProfileViewModel
import com.example.socialapp.android.auth.login.LoginViewModel
import com.example.socialapp.android.auth.signup.SignupViewModel
import com.example.socialapp.common.data.UserSettingsSerializer
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
    viewModel {HomeScreenViewModel()}
    viewModel {PostDetailScreenViewModel()}
    viewModel {ProfileViewModel()}
    viewModel {EditProfileViewModel()}
    viewModel {FollowsViewModel()}


}