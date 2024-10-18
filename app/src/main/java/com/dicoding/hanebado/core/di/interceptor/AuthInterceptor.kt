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
        val token = runBlocking {
            datastoreManager.getAccessToken().first().toString()
        }

        Log.d("AuthInterceptor", "Access token: $token")

        val request = chain.request().newBuilder()
        if (token.isNotEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(request.build())
    }
}