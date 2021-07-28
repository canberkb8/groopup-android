package com.android.groopup.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserGroupModel(
    @SerializedName("groupID")
    val groupID: String? = "",
    @SerializedName("groupTitle")
    val groupTitle: String? = "",
    @SerializedName("groupSubTitle")
    val groupSubTitle: String? = "",
    @SerializedName("groupImg")
    val groupImg: String? = "",
    @SerializedName("groupFavorite")
    val groupFavorite: Boolean? = false
): Parcelable
