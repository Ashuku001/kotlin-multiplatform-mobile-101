package com.example.socialapp.auth.data

import com.example.socialapp.auth.domain.model.AuthResultData
import com.example.socialapp.auth.domain.repository.AuthRepository
import com.example.socialapp.common.util.DispatcherProvider
import com.example.socialapp.common.util.Result
import kotlinx.coroutines.withContext

// internal cause the class within is internal
internal class AuthRepositoryImpl(
    private val dispatcher: DispatcherProvider,
    private val authService: AuthService
): AuthRepository  {

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<AuthResultData> {
        // switch the api call to bg thread from dispatchProvier
        return withContext(dispatcher.io){
            try {
                val request = SignUpRequest(name, email, password)

                val authResponse = authService.signUp(request)

                if (authResponse.data === null) {
                    Result.Error(
                        message = authResponse.errorMessage!! ?: ""
                    )
                } else {
                    Result.Success(
                        data = authResponse.data.toAuthResultData()
                    )
                }
            } catch(e: Exception) {
                Result.Error(
                    message = "Oops, we could not send your request. Try later"
                )
            }
        }
    }

    override suspend fun signIn(email: String, password: String): Result<AuthResultData> {
        return withContext(dispatcher.io){
            try {
                val request = SignInRequest( email, password)

                val authResponse = authService.signIn(request)

                if (authResponse.data === null) {
                    Result.Error(
                        message = authResponse.errorMessage!! ?: ""
                    )
                } else {
                    Result.Success(
                        data = authResponse.data.toAuthResultData()
                    )
                }
            } catch(e: Exception) {
                Result.Error(
                    message = "Oops, we could not send your request. Try later"
                )
            }
        }
    }
}