package com.android.groopup.ui.creategroup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.groopup.R
import com.android.groopup.core.BaseFragment
import com.android.groopup.data.remote.model.GroopUpAppData
import com.android.groopup.data.remote.model.GroupModel
import com.android.groopup.data.remote.model.UserGroupModel
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.databinding.FragmentCreateGroupBinding
import com.android.groopup.ui.profile.ProfileFragment
import com.android.groopup.utils.extensions.changeFragment
import com.android.groopup.utils.extensions.observeOnce
import com.android.groopup.utils.network.Resource
import com.android.groopup.utils.network.Status
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_persistent.*
import timber.log.Timber

@AndroidEntryPoint
class CreateGroupFragment : BaseFragment<FragmentCreateGroupBinding>() {

    private val createGroupViewModel: CreateGroupViewModel by viewModels()
    private val createGroupAdapter = CreateGroupAdapter()
    private var groupMemberList: ArrayList<UserModel> = arrayListOf()
    private var selectedUser: UserModel? = null
    private var groupImg: String = ""
    private lateinit var currentUser:UserModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var openCameraButton: LinearLayout
    private lateinit var openGalleryButton: LinearLayout

    companion object {
        private const val CAMERA_REQUEST_CODE = 101
        private const val GALLERY_REQUEST_CODE = 100
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_create_group
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        initBottomSheet()
        arguments?.let { it ->
            currentUser = CreateGroupFragmentArgs.fromBundle(it).userModel
            groupMemberList.add(currentUser)
            setAdapter()
        }

        click()
    }

    override fun onResume() {
        super.onResume()
        mainAct?.navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("KeyYes")
            ?.observe(viewLifecycleOwner, {
                it?.let {
                    if(GroopUpAppData.getKey()){
                        if (createGroupViewModel.checkIsUserInList(groupMemberList, selectedUser!!)) {
                            groupMemberList.add(selectedUser!!)
                            setAdapter()
                        } else {
                            Toast.makeText(requireContext(), "The user is already in the list.", Toast.LENGTH_LONG).show()
                        }
                        viewBinding.edtTxtEmail.setText("")
                        viewBinding.edtTxtEmail.clearFocus()
                        selectedUser = null
                        GroopUpAppData.setKey(false)
                    }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    uploadImg(mainAct?.util?.getUriFromBitmap(requireContext(), bitmap)!!)
                }
                GALLERY_REQUEST_CODE -> {
                    uploadImg(data?.data!!)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
            if (groupImg == "") groupImg = "https://i.dlpng.com/static/png/6449915_preview.png"
            createGroup(
                GroupModel(
                    createGroupViewModel.generateGroupID(),
                    viewBinding.edtTxtGroupName.text.toString(),
                    viewBinding.edtTxtGroupTitle.text.toString(),
                    groupImg,
                    GroopUpAppData.getCurrentUser()?.userID,
                    arrayListOf(groupMemberList[0].userID!!),
                    createGroupViewModel.handleInviteList(groupMemberList)
                )
            )
        }
        viewBinding.imgCamera.setOnClickListener {
            val state = if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_COLLAPSED
            else
                BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.state = state
        }
        openCameraButton.setOnClickListener {
            cameraPermission()
        }
        openGalleryButton.setOnClickListener {
            galleryPermission()
        }
    }

    private fun cameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mainAct?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.CAMERA)
                requestPermissions(permissions, CAMERA_REQUEST_CODE)
            } else {
                openCamera()
            }
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun galleryPermission() {
        mainAct?.navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("KeyYes")?.removeObservers(viewLifecycleOwner)
        mainAct?.navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("KeyNo")?.removeObservers(viewLifecycleOwner)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mainAct?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, GALLERY_REQUEST_CODE)
            } else {
                openGallery()
            }
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun createGroup(groupModel: GroupModel) {
        createGroupViewModel.createGroup(groupModel).let {
            createGroupViewModel.createGroup.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        currentUser.userGroupList.add(UserGroupModel(groupModel.groupID!!,groupModel.groupTitle,groupModel.groupSubTitle,groupImg))
                        GroopUpAppData.setCurrentUser(currentUser)
                        createGroupViewModel.updateUser(currentUser)
                        sendInvite(groupModel)
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

    private fun sendInvite(groupModel:GroupModel){
        for(user in groupMemberList){
            if(user != currentUser){
                user.userInviteList.add(UserGroupModel(groupModel.groupID!!,groupModel.groupTitle,groupModel.groupSubTitle,groupImg))
                createGroupViewModel.updateUser(user)
            }
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

    private fun initBottomSheet() {
        openCameraButton = bottomSheet.findViewById(R.id.btn_open_camera) as LinearLayout
        openGalleryButton = bottomSheet.findViewById(R.id.btn_open_gallery) as LinearLayout

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> viewBinding.imgPicture.setImageResource(R.drawable.ic_close)
                    BottomSheetBehavior.STATE_COLLAPSED -> viewBinding.imgPicture.setImageResource(R.drawable.ic_camera_alt)
                    else -> "Persistent Bottom Sheet"
                }
            }
        })
    }


    private fun uploadImg(selectedImageUri: Uri) {
        try {
            Glide.with(requireActivity())
                .load(selectedImageUri)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .optionalCenterCrop()
                .into(viewBinding.imgGroupImage)
            mainAct?.dialogHelper?.showDialog()
            mainAct?.mStorageRef = FirebaseStorage.getInstance().reference
            val imfRef = mainAct?.mStorageRef?.child("groups/groupImg")
                ?.child(java.util.UUID.randomUUID().toString())!!
            imfRef.putFile(selectedImageUri).addOnSuccessListener {
                imfRef.downloadUrl.addOnSuccessListener {
                    groupImg = it.toString()
                    Toast.makeText(activity, "uploadSucces", Toast.LENGTH_SHORT).show()
                    mainAct?.dialogHelper?.dismissDialog()
                }
            }.addOnFailureListener {
                Toast.makeText(activity, "Upload failed...", Toast.LENGTH_SHORT).show()
                mainAct?.dialogHelper?.dismissDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
}