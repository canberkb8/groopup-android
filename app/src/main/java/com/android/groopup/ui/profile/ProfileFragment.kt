package com.android.groopup.ui.profile

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.android.groopup.R
import com.android.groopup.core.BaseFragment
import com.android.groopup.data.remote.model.GroopUpAppData
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.databinding.FragmentProfileBinding
import com.android.groopup.utils.extensions.changeFragment
import com.android.groopup.utils.extensions.withGlideOrEmpty
import com.android.groopup.utils.network.Status
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var userModel: UserModel
    private var userName:String = ""
    private var userEmail:String = ""
    private var userPhone:String = ""
    private var userImg:String = ""
    override fun getLayoutRes(): Int {
        return R.layout.fragment_profile
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.lifecycleOwner = viewLifecycleOwner

        arguments?.let { it->
            userModel = ProfileFragmentArgs.fromBundle(it).userModel
            viewBinding.edtTxtUsername.setText(userModel.userName)
            viewBinding.edtTxtEmail.setText(userModel.userEmail)
            viewBinding.edtTxtPhone.setText(userModel.userPhone)
            withGlideOrEmpty(viewBinding.imgUserImage,userModel.userImage)
        }

        click()
    }

    private fun click() {
        viewBinding.imgBackpress.setOnClickListener {
            activity?.onBackPressed()
        }
        viewBinding.btnSignOut.setOnClickListener {
            mainAct?.sharedPreferencesHelper?.clear()
            mainAct?.auth?.signOut()
            activity?.changeFragment(R.id.loginFragment)
        }
        viewBinding.txtSave.setOnClickListener {
            userEmail = viewBinding.edtTxtEmail.text.toString().trim()
            userName = viewBinding.edtTxtUsername.text.toString().trim()
            userPhone = viewBinding.edtTxtPhone.text.toString().trim()
            updateUser(UserModel(mainAct?.auth?.uid!!,userEmail,userName,userImg,userPhone))
        }

    }

    private fun updateUser(userModel:UserModel){
        mainAct?.dialogHelper?.showDialog()
        mainAct?.auth?.currentUser?.updateEmail(userModel.userEmail!!)?.addOnCompleteListener {
            if (it.isSuccessful){
                profileViewModel.updateUser(userModel).let {
                    Toast.makeText(mainAct,"Profile Updated",Toast.LENGTH_LONG).show()
                    GroopUpAppData.setCurrentUser(userModel)
                    mainAct?.sharedPreferencesHelper?.saveUserUpdateProfile(true)
                    mainAct?.dialogHelper?.dismissDialog()
                }
            }else{
                mainAct?.dialogHelper?.dismissDialog()
                createAlertDialog("Do you want to log out?", it.exception?.message!!)
            }
        }
    }

    private fun createAlertDialog(title: String, subTitle: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(subTitle)
            .setNeutralButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Accept") { dialog, which ->
                mainAct?.sharedPreferencesHelper?.clear()
                mainAct?.auth?.signOut()
                activity?.changeFragment(R.id.loginFragment)
            }
            .show()
    }
}