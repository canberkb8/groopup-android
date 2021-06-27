package com.android.groopup.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class SharedPreferencesHelper@Inject constructor(@ActivityScoped private val activity: Activity) {
    private var sharedPreferences: SharedPreferences? = activity.getSharedPreferences("com.android.groopup", Context.MODE_PRIVATE)
    private var editor = sharedPreferences?.edit()

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_LOGIN = "user_login"
        const val USER_UPDATE_PROFILE = "user_update_profile"
    }

    fun saveUserToken(token: String) {
        editor?.putString(USER_TOKEN, token)?.apply()
    }

    fun getUserToken(): String? {
        return sharedPreferences?.getString(USER_TOKEN, null)
    }

    fun saveUserLogin(isLogin: Boolean) {
        editor?.putBoolean(USER_LOGIN, isLogin)?.apply()
    }

    fun getUserLogin(): Boolean? {
        return sharedPreferences?.getBoolean(USER_LOGIN, false)
    }

    fun saveUserUpdateProfile(isUpdate: Boolean) {
        editor?.putBoolean(USER_UPDATE_PROFILE, isUpdate)?.apply()
    }

    fun getUserUpdateProfile(): Boolean? {
        return sharedPreferences?.getBoolean(USER_UPDATE_PROFILE, false)
    }

    fun clear(){
        sharedPreferences?.edit()?.remove(USER_TOKEN)?.apply()
        sharedPreferences?.edit()?.remove(USER_LOGIN)?.apply()
        sharedPreferences?.edit()?.remove(USER_UPDATE_PROFILE)?.apply()
        editor?.clear()
    }
}