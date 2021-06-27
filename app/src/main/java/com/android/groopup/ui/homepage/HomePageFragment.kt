package com.android.groopup.ui.homepage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.android.groopup.R
import com.android.groopup.core.BaseFragment
import com.android.groopup.data.remote.model.HomePageModel
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.databinding.FragmentHomepageBinding
import com.android.groopup.utils.extensions.changeFragment
import com.android.groopup.utils.network.Status
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class HomePageFragment : BaseFragment<FragmentHomepageBinding>() {
    private val homePageViewModel: HomePageViewModel by viewModels()
    private var userModel:UserModel?=null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_homepage
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        viewBinding.txtAppVersion.text = mainAct?.util?.getAppVersion()
        if(userModel == null || mainAct?.sharedPreferencesHelper?.getUserUpdateProfile() == true){
            getUserData(mainAct?.auth?.uid!!)
        }else{
            viewBinding.txtUserEmail.text = userModel!!.userEmail
            viewBinding.txtUserName.text = userModel!!.userName
        }
        //getJobList("deneme")
        //createGroup(HomePageModel("asd","asd","asd","asd","asd"),"deneme")
        click()
    }

    private fun click() {
        viewBinding.layoutTabbar.setOnClickListener {
            val action = userModel?.let { userModel ->
                HomePageFragmentDirections.actionHomePageFragmentToProfileFragment(
                    userModel
                )
            }
            action?.let { direction -> activity?.changeFragment(direction) }
        }
    }

    private fun getUserData(userID:String) {
        homePageViewModel.getUserData(userID)
        homePageViewModel.user.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { response ->
                        userModel = response
                        viewBinding.txtUserEmail.text = userModel!!.userEmail
                        viewBinding.txtUserName.text = userModel!!.userName
                        mainAct?.sharedPreferencesHelper?.saveUserUpdateProfile(false)
                        mainAct?.dialogHelper?.dismissDialog()
                    }
                }
                Status.LOADING -> {
                    Timber.i("Loading")
                    mainAct?.dialogHelper?.showDialog()
                }
                Status.ERROR -> {
                    Timber.i("Error")
                    mainAct?.dialogHelper?.dismissDialog()
                }
            }
        })
    }

    private fun getJobList(groupID:String) {
        homePageViewModel.getGroupData(groupID)
        homePageViewModel.group.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { response ->
                        Timber.i("Success")
                        Timber.i("response --->$response")
                        mainAct?.dialogHelper?.dismissDialog()
                    }
                }
                Status.LOADING -> {
                    Timber.i("Loading")
                    mainAct?.dialogHelper?.showDialog()
                }
                Status.ERROR -> {
                    Timber.i("Error")
                    mainAct?.dialogHelper?.dismissDialog()
                }
            }
        })
    }

    private fun createGroup(homePageModel:HomePageModel,groupID:String){
        homePageViewModel.createGroup(
            homePageModel,
            groupID
        ).let {
            Timber.i("Create Group Success")
        }
    }
}