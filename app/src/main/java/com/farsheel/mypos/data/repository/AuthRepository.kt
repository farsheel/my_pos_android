package com.farsheel.mypos.data.repository

import android.app.Application
import android.content.Context
import com.farsheel.mypos.data.local.PreferenceManager
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.data.remote.request.LoginRequest
import com.farsheel.mypos.data.remote.response.LoginResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AuthRepository(context: Application) :
    BaseRepository(context) {

    fun login(loginRequest: LoginRequest): Single<LoginResponse> {

        return ApiClient.getApiService(context).login(loginRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun setUserSession(loginResponse: LoginResponse) {
        PreferenceManager.setUserSession(context, loginResponse)
    }

    fun isUserSessionAvailable(): Boolean {
        return PreferenceManager.isUserSessionAvailable(context)
    }
}