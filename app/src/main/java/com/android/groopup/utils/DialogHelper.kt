package com.android.groopup.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.android.groopup.R
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class DialogHelper@Inject constructor(@ActivityScoped private val activity: Activity) {
    private var dialog: Dialog = Dialog(activity)

    init {
        val wlmp = dialog.window!!.attributes
        wlmp.gravity = Gravity.CENTER_HORIZONTAL
        dialog.window!!.attributes = wlmp
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setTitle(null)
        dialog.setCancelable(false)
        dialog.setOnCancelListener(null)
        val view: View = LayoutInflater.from(activity).inflate(R.layout.dialog_goopup_anim, null)
        dialog.setContentView(view)
    }

    fun showDialog(){
        dialog.show()
    }

    fun dismissDialog(){
        dialog.dismiss()
    }

}