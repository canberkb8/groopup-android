package com.android.groopup.ui.creategroup

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.groopup.R
import com.android.groopup.core.BaseFragment
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.databinding.FragmentCreateGroupBinding
import com.android.groopup.utils.network.Status
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CreateGroupFragment : BaseFragment<FragmentCreateGroupBinding>() {

    private val createGroupViewModel: CreateGroupViewModel by viewModels()
    private val createGroupAdapter = CreateGroupAdapter()
    private var groupMemberList: ArrayList<UserModel> = arrayListOf()
    override fun getLayoutRes(): Int {
        return R.layout.fragment_create_group
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.lifecycleOwner = viewLifecycleOwner

        arguments?.let { it ->
            groupMemberList.add(CreateGroupFragmentArgs.fromBundle(it).userModel)
            setAdapter()
        }

        click()
    }

    private fun click() {
        viewBinding.imgBackpress.setOnClickListener {
            activity?.onBackPressed()
        }
        viewBinding.btnSearch.setOnClickListener {
            val email = viewBinding.edtTxtEmail.text.toString()
            if (!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) searchUser(email)
            else Toast.makeText(requireContext(), "Email is Incorrect", Toast.LENGTH_LONG).show()
        }
    }

    private fun setAdapter() {
        createGroupAdapter.userList = groupMemberList
        viewBinding.recyclerAddMemberList.apply {
            setHasFixedSize(true)
            adapter = createGroupAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    //Remove Observers Muhabbeti yapılcak
    private fun searchUser(email: String) {
        createGroupViewModel.searchUser(email)
        createGroupViewModel.searchUser.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data != null) {
                        Timber.i("response --->${it.data}")
                        Timber.i("Success")
                    } else {
                        Toast.makeText(requireContext(), "No Account Found for This E-Mail", Toast.LENGTH_LONG).show()
                    }
                    mainAct?.dialogHelper?.dismissDialog()
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