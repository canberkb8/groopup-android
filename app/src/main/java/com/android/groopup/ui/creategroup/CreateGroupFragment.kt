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
import com.android.groopup.data.remote.model.GroopUpAppData
import com.android.groopup.data.remote.model.GroupModel
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.databinding.FragmentCreateGroupBinding
import com.android.groopup.utils.extensions.changeFragment
import com.android.groopup.utils.extensions.observeOnce
import com.android.groopup.utils.network.Resource
import com.android.groopup.utils.network.Status
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CreateGroupFragment : BaseFragment<FragmentCreateGroupBinding>() {

    private val createGroupViewModel: CreateGroupViewModel by viewModels()
    private val createGroupAdapter = CreateGroupAdapter()
    private var groupMemberList: ArrayList<UserModel> = arrayListOf()
    private var selectedUser: UserModel? = null
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

    override fun onResume() {
        super.onResume()
        mainAct?.navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("KeyYes")
            ?.observe(viewLifecycleOwner, {
                it?.let {
                    if (createGroupViewModel.checkIsUserInList(groupMemberList, selectedUser!!)) {
                        groupMemberList.add(selectedUser!!)
                        setAdapter()
                    } else {
                        Toast.makeText(requireContext(), "The user is already in the list.", Toast.LENGTH_LONG).show()
                    }
                    viewBinding.edtTxtEmail.setText("")
                    viewBinding.edtTxtEmail.clearFocus()
                    selectedUser = null
                }
            })
        mainAct?.navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("KeyNo")
            ?.observe(viewLifecycleOwner, {
                it?.let {
                    viewBinding.edtTxtEmail.setText("")
                    viewBinding.edtTxtEmail.clearFocus()
                    selectedUser = null
                }
            })
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
        viewBinding.imgCreate.setOnClickListener {
            createGroup(
                GroupModel(
                    createGroupViewModel.generateGroupID(),
                    viewBinding.edtTxtGroupName.text.toString(),
                    viewBinding.edtTxtGroupTitle.text.toString(),
                    "ImageLink",
                    GroopUpAppData.getCurrentUser()?.userID,
                    arrayListOf(groupMemberList[0]),
                    createGroupViewModel.handleInviteList(groupMemberList)
                )
            )
        }
    }

    private fun createGroup(groupModel: GroupModel) {
        createGroupViewModel.createGroup(groupModel).let {
            createGroupViewModel.createGroup.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        mainAct?.dialogHelper?.dismissDialog()
                    }
                    Status.LOADING -> {
                        mainAct?.dialogHelper?.showDialog()
                    }
                    Status.ERROR -> {
                        mainAct?.dialogHelper?.dismissDialog()
                    }
                }
            })
        }
    }

    private fun searchUser(email: String) {
        createGroupViewModel.removeUserObservers(viewLifecycleOwner)
        createGroupViewModel.searchUser(email)
        createGroupViewModel.searchUser.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data != null) {
                        if (it.data.userEmail != null) {
                            Timber.i("Success")
                            selectedUser = it.data
                            val action = CreateGroupFragmentDirections.actionCreateGroupFragmentToCreateGroupAddMemberDialog(it.data)
                            activity?.changeFragment(action)
                            createGroupViewModel.searchUserData.postValue(Resource.success(null))
                        }
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
}