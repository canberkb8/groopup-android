package com.android.groopup.utils.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.android.groopup.R
import kotlinx.android.synthetic.main.activity_main.*

fun Activity.changeFragment(navAction: NavDirections) {
    this.findNavController(R.id.nav_host_fragment).navigate(navAction)
}

fun Activity.changeFragment(fragmentId: Int) {
    this.findNavController(R.id.nav_host_fragment).navigate(fragmentId)
}

/*
fun Activity.setNavbarVisible(visible:Int){
    this.bottom_nav_view.visibility = visible
}
*/

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}