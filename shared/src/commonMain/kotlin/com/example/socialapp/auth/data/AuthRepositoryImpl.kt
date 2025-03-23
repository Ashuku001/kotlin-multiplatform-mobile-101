package com.example.socialapp.auth.data

import com.example.socialapp.auth.domain.model.AuthResultData
import com.example.socialapp.auth.domain.repository.AuthRepository
import com.example.socialapp.common.data.local.UserPreferences
import com.example.socialapp.common.data.local.toUserSettings
import com.example.socialapp.common.util.DispatcherProvider
import com.example.socialapp.common.util.Result
import kotlinx.coroutines.withContext

// internal cause the class within is internal
internal class AuthRepositoryImpl(
    private val dispatcher: DispatcherProvider,
    private val authService: AuthService,
    private val userPreferences: UserPreferences
): AuthRepository  {

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<AuthResultData> {
        // switch the api call to bg thread from dispatch Provider
        return withContext(dispatcher.io){
            try {
                val request = SignUpRequest(name, email, password)

                val authResponse = authService.signUp(request)


                if (authResponse.data == null) {
                    Result.Error(
                        message = authResponse.errorMessage!! ?: ""
                    )
                } else {
                    // write the data store with the auth data
                    userPreferences.setUserData(
                        authResponse.data.toAuthResultData().toUserSettings()
                    )
                    Result.Success(
                        data = authResponse.data.toAuthResultData()
                    )
                }
            } catch(e: Exception) {
                println("EXCEPTION $e")
                Result.Error(
                    message = "Oops, we could not send your request. Try later"
                )
            }
        }
    }

    override suspend fun signIn(email: String, password: String): Result<AuthResultData> {
        return withContext(dispatcher.io){
            try {
                println(">>>>>>>>> $email , $password")
                val request = SignInRequest( email, password)

                println(">>>request>>>>>> $request")

                val authResponse = authService.signIn(request)

                println("response $authResponse")

                if (authResponse.data == null) {
                    Result.Error(
                        message = authResponse.errorMessage!! ?: ""
                    )
                } else {
                    // write the data store with the auth data
                    userPreferences.setUserData(
                        authResponse.data.toAuthResultData().toUserSettings()
                    )
                    Result.Success(
                        data = authResponse.data.toAuthResultData()
                    )
                }
            } catch(e: Exception) {
                println("EXCEPTION $e")
                Result.Error(
                    message = "Oops, we could not send your request. Try later"
                )
            }
        }
    }
}