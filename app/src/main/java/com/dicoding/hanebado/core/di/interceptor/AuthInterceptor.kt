package com.dicoding.hanebado.core.di.interceptor

import android.util.Log
import com.dicoding.hanebado.core.data.source.local.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val datastoreManager: DataStoreManager) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url.toString()

        // Bypass auth untuk endpoint aktivasi
        if (requestUrl.contains("/users/") && originalRequest.method == "GET") {
            Log.d("AuthInterceptor", "Bypassing auth for activation check endpoint")
            return chain.proceed(originalRequest)
        }

        val token = runBlocking {
            datastoreManager.getAccessToken().first().toString()
        }

        Log.d("AuthInterceptor", "Access token: $token")

        return if (token.isNotEmpty()) {
            val authorizedRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(authorizedRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}