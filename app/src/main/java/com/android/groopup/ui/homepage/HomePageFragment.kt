package com.android.groopup.ui.homepage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.groopup.R
import com.android.groopup.core.BaseFragment
import com.android.groopup.data.remote.model.GroopUpAppData
import com.android.groopup.data.remote.model.GroupModel
import com.android.groopup.data.remote.model.UserGroupModel
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.databinding.FragmentHomepageBinding
import com.android.groopup.ui.creategroup.CreateGroupAdapter
import com.android.groopup.utils.extensions.changeFragment
import com.android.groopup.utils.extensions.observeOnce
import com.android.groopup.utils.extensions.withGlideOrEmpty
import com.android.groopup.utils.network.Status
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.HashMap

@AndroidEntryPoint
class HomePageFragment : BaseFragment<FragmentHomepageBinding>() {
    private val homePageViewModel: HomePageViewModel by viewModels()
    private var userModel: UserModel? = null
    private val homePageAdapter = HomePageAdapter()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_homepage
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        viewBinding.txtAppVersion.text = mainAct?.util?.getAppVersion()

        if (mainAct?.auth?.uid != null){
            getUserData(mainAct?.auth?.uid!!)
        }else{
            mainAct?.changeFragment(R.id.loginFragment)
        }
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
            if (item.userID == userID){
                if (mainAct?.sharedPreferencesHelper?.getUserUpdateProfile() == true) {
                    GroopUpAppData.updateUserForList(index,GroopUpAppData.getCurrentUser()!!)
                    userModel = GroopUpAppData.getCurrentUser()!!
                } else {
                    userModel = item
                    GroopUpAppData.setCurrentUser(userModel)
                }
                setAdapter(userModel?.userGroupList!!)
            }
        }
        viewBinding.txtUserEmail.text = userModel!!.userEmail
        viewBinding.txtUserName.text = userModel!!.userName
        withGlideOrEmpty(viewBinding.imgUserImage,userModel!!.userImage)
        mainAct?.sharedPreferencesHelper?.saveUserUpdateProfile(false)
    }

    private fun profileFragmentAction(){
        val action = userModel?.let { userModel ->
            HomePageFragmentDirections.actionHomePageFragmentToProfileFragment(
                userModel
            )
        }
        action?.let { direction -> activity?.changeFragment(direction) }
    }

    private fun setAdapter(userGroupModel: HashMap<String,UserGroupModel>) {
        homePageAdapter.userGroupList = homePageViewModel.generateHashToArray(userGroupModel)
        viewBinding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = homePageAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }

}
