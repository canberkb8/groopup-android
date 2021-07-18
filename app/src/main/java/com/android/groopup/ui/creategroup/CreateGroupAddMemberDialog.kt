package com.android.groopup.ui.creategroup

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.android.groopup.R
import com.android.groopup.data.remote.model.GroopUpAppData
import com.android.groopup.databinding.DialogCreateGroupAddMemberBinding
import com.android.groopup.ui.MainActivity
import com.android.groopup.utils.extensions.withGlideOrEmpty

class CreateGroupAddMemberDialog : DialogFragment() {

    private lateinit var viewBinding: DialogCreateGroupAddMemberBinding
    var mainActivity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.dialog_create_group_add_member,
                container,
                false
            )
        return viewBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(85)
        arguments?.let {
            val user = CreateGroupFragmentArgs.fromBundle(it).userModel
            viewBinding.txtMemberEmail.text = user.userEmail
            viewBinding.txtMemberUsername.text = user.userName
            withGlideOrEmpty(viewBinding.imgMemberImage, user.userImage)
        }
        click()
    }

    private fun click() {
        viewBinding.btnYes.setOnClickListener {
            GroopUpAppData.setKey(true)
            if(GroopUpAppData.getKey()){
                mainActivity?.navController?.previousBackStackEntry?.savedStateHandle?.set(
                    "KeyYes",
                    true
                )
            }
            dismiss()
        }
        viewBinding.btnNo.setOnClickListener {
            GroopUpAppData.setKey(false)
            if(!GroopUpAppData.getKey()){
                mainActivity?.navController?.previousBackStackEntry?.savedStateHandle?.set(
                    "KeyNo",
                    true
                )
            }
            dismiss()
        }
    }

    private fun DialogFragment.setWidthPercent(percentage: Int) {
        val percent = percentage.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * percent
        dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}