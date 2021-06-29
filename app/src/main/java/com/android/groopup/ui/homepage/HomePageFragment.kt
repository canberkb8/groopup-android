package com.android.groopup.ui.homepage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.android.groopup.R
import com.android.groopup.core.BaseFragment
import com.android.groopup.data.remote.model.GroopUpAppData
import com.android.groopup.data.remote.model.GroupModel
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.databinding.FragmentHomepageBinding
import com.android.groopup.utils.extensions.changeFragment
import com.android.groopup.utils.network.Status
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.HashMap

@AndroidEntryPoint
class HomePageFragment : BaseFragment<FragmentHomepageBinding>() {
    private val homePageViewModel: HomePageViewModel by viewModels()
    private var userModel: UserModel? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_homepage
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        viewBinding.txtAppVersion.text = mainAct?.util?.getAppVersion()

        getUserData(mainAct?.auth?.uid!!)

        //getJobList("deneme")
        //createGroup(GroupModel("asd","asd","asd","asd","asd"),"deneme")
        click()
    }

    private fun click() {
        viewBinding.layoutTabbar.setOnClickListener {
            profileFragmentAction()
        }
        viewBinding.imgUserImage.setOnClickListener {
            profileFragmentAction()
        }
        viewBinding.btnCreateGroup.setOnClickListener {
            val action = userModel?.let { userModel ->
                HomePageFragmentDirections.actionHomePageFragmentToCreateGroupFragment(
                    userModel
                )
            }
            action?.let { direction -> activity?.changeFragment(direction) }
        }
    }

    private fun getUserData(userID: String) {
        for ((index, item) in GroopUpAppData.getUserList().withIndex()) {
            if (item.userID == userID)
                if (mainAct?.sharedPreferencesHelper?.getUserUpdateProfile() == true) {
                    GroopUpAppData.updateUserForList(index,GroopUpAppData.getCurrentUser()!!)
                    userModel = GroopUpAppData.getCurrentUser()!!
                } else {
                    userModel = item
                    GroopUpAppData.setCurrentUser(userModel)
                }
        }
        viewBinding.txtUserEmail.text = userModel!!.userEmail
        viewBinding.txtUserName.text = userModel!!.userName
        mainAct?.sharedPreferencesHelper?.saveUserUpdateProfile(false)
    }

    private fun getJobList(groupID: String) {
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

    private fun createGroup(groupModel: GroupModel, groupID: String) {
        homePageViewModel.createGroup(
            groupModel,
            groupID
        ).let {
            Timber.i("Create Group Success")
        }
    }

    private fun profileFragmentAction(){
        val action = userModel?.let { userModel ->
            HomePageFragmentDirections.actionHomePageFragmentToProfileFragment(
                userModel
            )
        }
        action?.let { direction -> activity?.changeFragment(direction) }
    }
}
