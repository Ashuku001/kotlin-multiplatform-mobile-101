package com.example.socialapp.common.data.local

// preference filename
internal const val PREFERENCE_FILE_NAME = "app_user_settings.preference_pb"

// setup up preferences
// on android we use kotlinx serialization but on IOS since we cant use kotlinx hence we set it up manually
internal interface UserPreferences {
    suspend fun getUserData(): UserSettings

    suspend fun setUserData(userSettings: UserSettings)
}