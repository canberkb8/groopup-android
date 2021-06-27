package com.android.groopup.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.groopup.R
import com.android.groopup.core.BaseActivity
import com.android.groopup.databinding.ActivityMainBinding
import com.android.groopup.utils.DialogHelper
import com.android.groopup.utils.SharedPreferencesHelper
import com.android.groopup.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    lateinit var auth: FirebaseAuth
    var navController: NavController? = null
    @Inject
    lateinit var dialogHelper: DialogHelper
    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    @Inject
    lateinit var util: Utils

    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
    }

    override fun onBackPressed() {
        if (navController?.currentDestination?.id == R.id.homePageFragment || navController?.currentDestination?.id == R.id.loginFragment) {
            finish()
        }else {
            super.onBackPressed();
        }
    }
}