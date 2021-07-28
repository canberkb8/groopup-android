package com.android.groopup.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

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
    var userGroupList:@RawValue HashMap<String,UserGroupModel>?= null,
    @SerializedName("userInviteList")
    var userInviteList:@RawValue HashMap<String,UserGroupModel>?= null
) : Parcelable