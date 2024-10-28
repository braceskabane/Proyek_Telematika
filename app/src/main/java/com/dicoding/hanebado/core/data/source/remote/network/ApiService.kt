package com.dicoding.hanebado.core.data.source.remote.network

import com.dicoding.hanebado.core.data.source.remote.response.ActiveResponse
import com.dicoding.hanebado.core.data.source.remote.response.LoginResponse
import com.dicoding.hanebado.core.data.source.remote.response.OtpResponse
import com.dicoding.hanebado.core.data.source.remote.response.RegisterResponse
import com.dicoding.hanebado.core.data.source.remote.response.ResendOtpResponse
import com.dicoding.hanebado.core.data.source.remote.response.UserResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // Auth
    @FormUrlEncoded
    @POST("users/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("users/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("users/activate")
    suspend fun otp(
        @Field("userId") userId: String,
        @Field("otpCode") otpCode: String
    ): OtpResponse

    @FormUrlEncoded
    @GET("users/me")
    suspend fun user(
        // masukan token yang didapat saat login
    ): UserResponse

    @POST("users/resend-otp/{userId}")
    suspend fun resendOTP(
        @Path("userId") userId: String
    ): ResendOtpResponse

    @GET("users/{email}")
    suspend fun activateCheck(
        @Path("email") email: String
    ): ActiveResponse

    ///////////////////////////////////////////////////////////////////////////////

}