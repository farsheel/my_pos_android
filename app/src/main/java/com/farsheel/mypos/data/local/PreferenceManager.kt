package com.farsheel.mypos.data.local

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.farsheel.mypos.data.remote.response.LoginResponse

class PreferenceManager {
    companion object {
        private const val PREF_NAME = "my_pos"

        private const val PREF_TOKEN = "PREF_TOKEN"
        private const val PREF_EMAIL = "PREF_EMAIL"
        private const val PREF_FULL_NAME = "PREF_FULL_NAME"
        private const val PREF_USER_ID = "PREF_USER_ID"

        fun setUserSession(application: Context, loginResponse: LoginResponse) {
            val mySharedPreferences: SharedPreferences =
                application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val myEditor: SharedPreferences.Editor = mySharedPreferences.edit()
            myEditor.putString(PREF_TOKEN, loginResponse.accessToken)
            myEditor.putString(PREF_EMAIL, loginResponse.data.email)
            myEditor.putString(PREF_FULL_NAME, loginResponse.data.name)
            myEditor.putString(PREF_USER_ID, loginResponse.data.id)
            myEditor.apply()
        }

        fun isUserSessionAvailable(application: Context): Boolean {
            val mySharedPreferences: SharedPreferences =
                application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return mySharedPreferences.getString(PREF_TOKEN, null) != null
        }

        fun getUserToken(application: Context): String? {
            val mySharedPreferences: SharedPreferences =
                application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return mySharedPreferences.getString(PREF_TOKEN, "")
        }
    }
}