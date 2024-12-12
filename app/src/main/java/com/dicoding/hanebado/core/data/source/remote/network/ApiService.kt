package com.dicoding.hanebado.core.data.source.remote.network

import com.dicoding.hanebado.core.data.source.remote.response.auth.ActiveResponse
import com.dicoding.hanebado.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.hanebado.core.data.source.remote.response.auth.OtpResponse
import com.dicoding.hanebado.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.hanebado.core.data.source.remote.response.auth.ResendOtpResponse
import com.dicoding.hanebado.core.data.source.remote.response.auth.UserResponse
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.AddDailyPlanResponse
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.DailyPlanRequest
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.GetAllDailyResponse
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.GetTodayResponse
import com.dicoding.hanebado.core.data.source.remote.response.exercise.ExerciseResponse
import com.dicoding.hanebado.core.data.source.remote.response.histories.HistoryResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    //////////////////////////////////////////// Auth
    @FormUrlEncoded
//    @POST("users/login")
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
//    @POST("users/register")
    @POST("auth/register")
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
    ): UserResponse

    @POST("users/resend-otp/{userId}")
    suspend fun resendOTP(
        @Path("userId") userId: String
    ): ResendOtpResponse

    @GET("users/{email}")
    suspend fun activateCheck(
        @Path("email") email: String
    ): ActiveResponse

    //////////////////////////////////////////// Daily Plan
    @POST("/daily-plans")
    suspend fun sendDailyPlan(
        @Body dailyPlanRequest: DailyPlanRequest
    ): AddDailyPlanResponse

    @GET("/daily-plans/")
    suspend fun getAllDailyPlan(

    ): GetAllDailyResponse

    @GET("/daily-plans/today")
    suspend fun getTodayDailyPlan(

    ): GetTodayResponse

    //////////////////////////////////////////// Exercise
    @GET("/exercises")
    suspend fun allExercise(
        // Masukkan token yang didapat saat login
    ):ExerciseResponse

    // Histories
    @GET("/history/")
    suspend fun history(): HistoryResponse

    //////////////////////////////////////////// Session
    @FormUrlEncoded
    @POST("/sessions/start")
    suspend fun exerciseSave(
        @Field("dailyPlanId") dailyPlanId: String,
        @Field("exerciseId") exerciseId: String,
        @Field("setNumber") setNumber: String,
    ): OtpResponse

    ///////////////////////////////////////////////////////////////////////////////

}