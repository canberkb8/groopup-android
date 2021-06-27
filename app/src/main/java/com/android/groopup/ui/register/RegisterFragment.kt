package com.android.groopup.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.android.groopup.R
import com.android.groopup.core.BaseFragment
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.databinding.FragmentRegisterBinding
import com.android.groopup.ui.homepage.HomePageViewModel
import com.android.groopup.utils.extensions.changeFragment
import com.android.groopup.utils.network.Status
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    private val registerViewModel: RegisterViewModel by viewModels()
    override fun getLayoutRes(): Int {
        return R.layout.fragment_register
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        click()
    }

    private fun click() {
        viewBinding.btnRegister.setOnClickListener {
            createAuthUser(viewBinding.edtTxtEmail.text.toString().trim(),viewBinding.edtTxtPassword.text.toString().trim(),viewBinding.edtTxtCheckPassword.text.toString().trim())
        }
        viewBinding.txtBackToSignIn.setOnClickListener {
            activity?.changeFragment(R.id.loginFragment)
        }
    }

    //Toastlar yerine dialoglar yap
    private fun createAuthUser(email:String?, password:String?, checkPassword:String?) {
        if (email != null){
            if (password != null){
                if(password == checkPassword){
                    mainAct?.dialogHelper?.showDialog()
                    mainAct?.auth?.createUserWithEmailAndPassword(email, password)
                        ?.addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                Timber.i("Success")
                                mainAct?.sharedPreferencesHelper?.saveUserLogin(true)
                                mainAct?.sharedPreferencesHelper?.saveUserToken(mainAct?.auth?.currentUser?.uid!!)
                                registerViewModel.createUser(UserModel(mainAct?.auth?.currentUser?.uid!!,email)).let {
                                    createUser()
                                }
                            } else {
                                Timber.i("Fail")
                                mainAct?.dialogHelper?.dismissDialog()
                            }
                        }
                }else{
                    Toast.makeText(mainAct,"Passwords do not match",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(mainAct,"Please enter password",Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(mainAct,"Please enter mail",Toast.LENGTH_LONG).show()
        }
    }

    private fun createUser(){
        registerViewModel.createUser.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    mainAct?.dialogHelper?.dismissDialog()
                    activity?.changeFragment(R.id.homePageFragment)
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
}