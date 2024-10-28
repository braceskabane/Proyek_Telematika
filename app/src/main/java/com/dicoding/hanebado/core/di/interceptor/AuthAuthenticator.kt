package com.dicoding.hanebado.core.di.interceptor

import android.util.Log
import com.dicoding.hanebado.core.data.source.local.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val datastoreManager: DataStoreManager,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // Bypass authenticator untuk endpoint aktivasi
        if (response.request.url.toString().contains("/users/") &&
            response.request.method == "GET") {
            Log.d("AuthAuthenticator", "Bypassing authenticator for activation check endpoint")
            return null
        }

        val accessToken = runBlocking {
            datastoreManager.getAccessToken().first().toString()
        }

        return if (response.code == 401 && accessToken.isNotEmpty()) {
            runBlocking {
                Log.e("AuthAuthenticator", "Token expired. Logging out user.")
                datastoreManager.deleteToken()
                datastoreManager.saveLoginStatus(false).first()
                null
            }
        } else {
            null
        }
    }
}