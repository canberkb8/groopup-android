package com.android.groopup.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Environment
import dagger.hilt.android.scopes.ActivityScoped
import java.io.File
import javax.inject.Inject

class Utils @Inject constructor(@ActivityScoped private val activity: Activity) {

    fun getAppVersion(): String {
        try {
            val pInfo =
                activity.packageManager?.getPackageInfo(activity.packageName.toString(), 0)
            return "version " + pInfo?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

}