package com.android.groopup.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    @SerializedName("userID")
    val userID: String? = null,
    @SerializedName("userEmail")
    val userEmail: String? = null,
    @SerializedName("userName")
    val userName:String="",
    @SerializedName("userImage")
    val userImage: String="https://www.freeiconspng.com/thumbs/profile-icon-png/profile-icon-9.png",
    @SerializedName("userPhone")
    val userPhone: String="",
    @SerializedName("userGroupList")
    var userGroupList:ArrayList<String> = arrayListOf(),
    @SerializedName("userInviteList")
    var userInviteList:ArrayList<String> = arrayListOf()
) : Parcelable