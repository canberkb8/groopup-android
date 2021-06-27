package com.android.groopup.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.android.groopup.R
import com.android.groopup.core.BaseFragment
import com.android.groopup.databinding.FragmentSplashBinding
import com.android.groopup.utils.extensions.changeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val splashTime:Long = 3000
    private val splashHandler = Handler(Looper.getMainLooper())

    override fun getLayoutRes(): Int {
        return R.layout.fragment_splash
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.lifecycleOwner = viewLifecycleOwner

        splashHandler()
    }

    private fun splashHandler(){
        splashHandler.postDelayed({
            if (mainAct?.sharedPreferencesHelper?.getUserLogin() == true && mainAct?.sharedPreferencesHelper?.getUserToken() != null) {
                activity?.changeFragment(R.id.homePageFragment)
            }else{
                mainAct?.changeFragment(R.id.loginFragment)
            }
        }, splashTime)
    }

}