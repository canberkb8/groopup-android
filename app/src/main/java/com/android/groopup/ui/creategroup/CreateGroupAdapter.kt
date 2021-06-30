package com.android.groopup.ui.creategroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.groopup.data.remote.model.GroopUpAppData
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.databinding.CardCreateGroupMemberItemBinding
import com.android.groopup.utils.extensions.withGlideOrEmpty

class CreateGroupAdapter : RecyclerView.Adapter<CreateGroupAdapter.CreateGroupViewHolder>() {

    private lateinit var cardCreateGroupMemberItemBinding: CardCreateGroupMemberItemBinding

    private var onItemClickListener: ((UserModel) -> Unit)? = null

    var userList: MutableList<UserModel> = mutableListOf()
        set(value) {
            field = value
            this.notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateGroupViewHolder {
        cardCreateGroupMemberItemBinding = CardCreateGroupMemberItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CreateGroupViewHolder(cardCreateGroupMemberItemBinding)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: CreateGroupViewHolder, position: Int) {
        val userItem = userList[position]
        userItem.let {
            holder.bind(it, onItemClickListener)
        }
    }

    class CreateGroupViewHolder(private val cardCreateGroupMemberItemBinding: CardCreateGroupMemberItemBinding) :
        RecyclerView.ViewHolder(cardCreateGroupMemberItemBinding.root) {
        fun bind(userModel: UserModel, onItemClickListener: ((userModel: UserModel) -> Unit)?) {
            withGlideOrEmpty(cardCreateGroupMemberItemBinding.imgMemberImage,userModel.userImage)
            if (userModel.userID == GroopUpAppData.getCurrentUser()?.userID){
                cardCreateGroupMemberItemBinding.imgRemove.visibility = View.GONE
            }else{
                cardCreateGroupMemberItemBinding.imgRemove.visibility = View.VISIBLE
            }
            cardCreateGroupMemberItemBinding.imgRemove.setOnClickListener { onItemClickListener?.invoke(userModel) }
        }

    }

    fun setOnItemClickListener(onItemClickListener: ((UserModel) -> Unit)?) {
        this.onItemClickListener = onItemClickListener
    }

}