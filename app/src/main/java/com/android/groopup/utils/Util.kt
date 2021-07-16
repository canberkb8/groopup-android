package com.android.groopup.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.scopes.ActivityScoped
import java.io.ByteArrayOutputStream
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

    fun getUriFromBitmap(inContext: Context, inImage: Bitmap) : Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
}