package com.android.groopup.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.groopup.R
import com.android.groopup.core.BaseFragment
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.databinding.FragmentLoginBinding
import com.android.groopup.ui.homepage.HomePageViewModel
import com.android.groopup.utils.extensions.changeFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val loginViewModel: LoginViewModel by viewModels()
    override fun getLayoutRes(): Int {
        return R.layout.fragment_login
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        click()
    }

    private fun click() {
        viewBinding.txtRegister.setOnClickListener {
            activity?.changeFragment(R.id.registerFragment)
        }
        viewBinding.btnSignIn.setOnClickListener {
            login(
                viewBinding.edtTxtEmail.text.toString().trim(),
                viewBinding.edtTxtPassword.text.toString().trim()
            )
        }
    }

    private fun login(email: String?, password: String?) {
        if (email != null) {
            if (password != null) {
                mainAct?.dialogHelper?.showDialog()
                mainAct?.auth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(requireActivity()) { task ->
                        mainAct?.dialogHelper?.dismissDialog()
                        if (task.isSuccessful) {
                            Timber.i("Success")
                            mainAct?.sharedPreferencesHelper?.saveUserLogin(true)
                            mainAct?.sharedPreferencesHelper?.saveUserToken(mainAct?.auth?.currentUser?.uid!!)
                            activity?.changeFragment(R.id.homePageFragment)
                        } else {
                            Timber.i("Fail")
                        }
                    }
            }
        }
    }
}