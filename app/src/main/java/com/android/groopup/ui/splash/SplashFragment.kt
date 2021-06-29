package com.android.groopup.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.android.groopup.R
import com.android.groopup.core.BaseFragment
import com.android.groopup.data.remote.model.GroopUpAppData
import com.android.groopup.databinding.FragmentSplashBinding
import com.android.groopup.ui.creategroup.CreateGroupViewModel
import com.android.groopup.utils.extensions.changeFragment
import com.android.groopup.utils.network.Status
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val splashTime: Long = 2000
    private val splashHandler = Handler(Looper.getMainLooper())

    override fun getLayoutRes(): Int {
        return R.layout.fragment_splash
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        getUserList()
    }

    private fun splashHandler() {
        splashHandler.postDelayed({
            if (mainAct?.sharedPreferencesHelper?.getUserLogin() == true && mainAct?.sharedPreferencesHelper?.getUserToken() != null) {
                activity?.changeFragment(R.id.homePageFragment)
            } else {
                mainAct?.changeFragment(R.id.loginFragment)
            }
        }, splashTime)
    }


    private fun getUserList() {
        splashViewModel.userList.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let {
                        splashHandler()
                    }
                }
                Status.LOADING -> {
                    Timber.i("Loading")
                }
                Status.ERROR -> {
                    createAlertDialog("There is Something Wrong", "Please restart the application")
                }
            }
        })
    }

    private fun createAlertDialog(title: String, subTitle: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(subTitle)
            .setPositiveButton("OK") { dialog, which ->
                activity?.finish()
            }
            .show()
    }

}