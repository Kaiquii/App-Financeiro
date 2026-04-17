package com.example.appfinanceiro.core.network.auth

import com.example.appfinanceiro.BuildConfig
import com.example.appfinanceiro.core.network.FinanceApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)
data class ResetPasswordRequest(val email: String, val old_password: String, val new_password: String)

data class LoginResponse(val message: String, val token: String)
data class RegisterResponse(val message: String, val user_id: Int)
data class ResetPasswordResponse(val message: String)

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @PATCH("api/auth/users")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse
}

object RetrofitClient {
    val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    val financeApi: FinanceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FinanceApi::class.java)
    }
}