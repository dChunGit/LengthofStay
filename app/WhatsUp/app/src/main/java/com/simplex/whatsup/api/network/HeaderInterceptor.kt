package com.simplex.whatsup.api.network

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor: Interceptor {
    private var sessionToken: String = ""

    fun updateToken(newToken: String?) {
        newToken?.apply { sessionToken = newToken }
    }

    fun clearToken() {
        sessionToken = ""
    }

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request().newBuilder()
                .apply {
                    header("Mobile", "Android")
                    if (sessionToken != "") {
                        header("Cookie", "token=$sessionToken")
                    }
                }
                .build()
        )
    }
}