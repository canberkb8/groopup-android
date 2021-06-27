package com.android.groopup.data.remote.model

import com.google.gson.annotations.SerializedName

data class HomePageModel(
    @SerializedName("groupID")
    val groupID:String?=null,
    @SerializedName("groupFounderID")
    val groupFounderID:String?=null,
    @SerializedName("groupImg")
    val groupImg:String?=null,
    @SerializedName("groupTitle")
    val groupTitle:String?=null,
    @SerializedName("groupSubTitle")
    val groupSubTitle:String?=null
)
