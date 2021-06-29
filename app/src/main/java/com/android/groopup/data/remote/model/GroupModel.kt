package com.android.groopup.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroupModel(
    @SerializedName("groupID")
    val groupID:String?=null,
    @SerializedName("groupTitle")
    val groupTitle:String?=null,
    @SerializedName("groupSubTitle")
    val groupSubTitle:String?=null,
    @SerializedName("groupImg")
    val groupImg:String?=null,
    @SerializedName("groupFounderID")
    val groupFounderID:String?=null,
    @SerializedName("groupMemberList")
    val groupMemberList:ArrayList<UserModel>? = null,
    @SerializedName("groupInviteList")
    val groupInviteList:ArrayList<UserModel>? = null,
) : Parcelable
