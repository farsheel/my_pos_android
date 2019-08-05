package com.farsheel.mypos.data.remote

import android.app.Application
import com.farsheel.mypos.BuildConfig
import com.farsheel.mypos.data.local.PreferenceManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    companion object {
        private const val BASE_URL = BuildConfig.API_URL

        private fun create(application: Application): Retrofit {
            return Retrofit.Builder()
                .client(
                    OkHttpClient().newBuilder()
                        .addInterceptor(HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })
                        .addInterceptor(HeaderInterceptor(application))
                        .build()
                )
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }


        fun getApiService(application: Application): WebServices {
            return create(application).create(WebServices::class.java)
        }
    }

    class HeaderInterceptor(val application: Application) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder = chain.request().newBuilder()
            builder.addHeader("Accept", "application/json")
            builder.addHeader(
                "Authorization",
                "Bearer " + PreferenceManager.getUserToken(application)
            )
            return chain.proceed(builder.build())
        }
    }
}