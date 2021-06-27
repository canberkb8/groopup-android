package com.android.groopup.utils

import android.app.Activity
import android.content.pm.PackageManager
import com.android.groopup.R
import com.android.groopup.utils.extensions.changeFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.scopes.ActivityScoped
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