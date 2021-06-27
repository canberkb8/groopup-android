package com.android.groopup.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    @SerializedName("userID")
    val userID: String,
    @SerializedName("userEmail")
    val userEmail: String,
    @SerializedName("userName")
    val userName:String="",
    @SerializedName("userImage")
    val userImage: String="",
    @SerializedName("userPhone")
    val userPhone: String="",
    @SerializedName("userGroupList")
    val userGroupList:ArrayList<String>? = null
) : Parcelable