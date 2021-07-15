package com.android.groopup.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.android.groopup.R
import com.android.groopup.core.BaseFragment
import com.android.groopup.data.remote.model.GroopUpAppData
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.databinding.FragmentProfileBinding
import com.android.groopup.utils.extensions.changeFragment
import com.android.groopup.utils.extensions.withGlideOrEmpty
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_persistent.*
import timber.log.Timber
import java.io.File


@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var userModel: UserModel
    private lateinit var filePhoto: File
    private var userName: String = ""
    private var userEmail: String = ""
    private var userPhone: String = ""
    private var userImg: String = ""
    private lateinit var selectedImageUri: Uri
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var openCameraButton: LinearLayout
    private lateinit var openGalleryButton: LinearLayout

    companion object {
        private const val IMAGE_CHOOSE = 1000
        private const val PERMISSION_CODE = 1001
        private const val REQUEST_CODE = 13
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_profile
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        initBottomSheet()
        arguments?.let { it ->
            userModel = ProfileFragmentArgs.fromBundle(it).userModel
            viewBinding.edtTxtUsername.setText(userModel.userName)
            viewBinding.edtTxtEmail.setText(userModel.userEmail)
            viewBinding.edtTxtPhone.setText(userModel.userPhone)
            withGlideOrEmpty(viewBinding.imgUserImage, userModel.userImage)
            userImg = userModel.userImage
        }
        click()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            if (requestCode == REQUEST_CODE) {
                val takenPhoto = BitmapFactory.decodeFile(filePhoto.absolutePath)
                viewBinding.imgUserImage.setImageBitmap(takenPhoto)
            } else if (requestCode == IMAGE_CHOOSE) {
                selectedImageUri = data?.data!!
                Glide.with(requireActivity())
                    .load(selectedImageUri)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .optionalCenterCrop()
                    .into(viewBinding.imgUserImage)
                uploadImg(selectedImageUri)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImageGallery()
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
        viewBinding.btnSignOut.setOnClickListener {
            mainAct?.sharedPreferencesHelper?.clear()
            mainAct?.auth?.signOut()
            activity?.changeFragment(R.id.loginFragment)
        }
        viewBinding.txtSave.setOnClickListener {
            userEmail = viewBinding.edtTxtEmail.text.toString().trim()
            userName = viewBinding.edtTxtUsername.text.toString().trim()
            userPhone = viewBinding.edtTxtPhone.text.toString().trim()
            updateUser(UserModel(mainAct?.auth?.uid!!, userEmail, userName, userImg, userPhone))
        }
        viewBinding.imgCamera.setOnClickListener {
            val state = if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_COLLAPSED
            else
                BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.state = state
        }
        openCameraButton.setOnClickListener {
            openCamera()
        }
        openGalleryButton.setOnClickListener {
            openGallery()
        }
    }

    private fun openCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mainAct?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.CAMERA)
                requestPermissions(permissions, REQUEST_CODE)
            } else {
                takePhoto()
            }
        } else {
            takePhoto()
        }
    }

    private fun takePhoto() {
        val FILE_NAME = "photo"
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        filePhoto = getPhotoFile(FILE_NAME)
        val providerFile = FileProvider.getUriForFile(
            requireContext(),
            "com.android.groopup.fileprovider",
            filePhoto
        )
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
    }

    private fun openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mainAct?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                chooseImageGallery();
            }
        } else {
            chooseImageGallery();
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = mainAct?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", directoryStorage)
    }

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
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
            mainAct?.dialogHelper?.showDialog()
            mainAct?.mStorageRef = FirebaseStorage.getInstance().reference
            val imfRef = mainAct?.mStorageRef?.child("users/profileImg")
                ?.child(java.util.UUID.randomUUID().toString())!!
            imfRef.putFile(selectedImageUri).addOnSuccessListener {
                imfRef.downloadUrl.addOnSuccessListener {
                    userImg = it.toString()
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

    private fun updateUser(userModel: UserModel) {
        mainAct?.dialogHelper?.showDialog()
        mainAct?.auth?.currentUser?.updateEmail(userModel.userEmail!!)?.addOnCompleteListener {
            if (it.isSuccessful) {
                profileViewModel.updateUser(userModel).let {
                    Toast.makeText(mainAct, "Profile Updated", Toast.LENGTH_LONG).show()
                    GroopUpAppData.setCurrentUser(userModel)
                    mainAct?.sharedPreferencesHelper?.saveUserUpdateProfile(true)
                    mainAct?.dialogHelper?.dismissDialog()
                }
            } else {
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