package com.android.groopup.data.remote.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserGroupModel(
    val groupID: String? = "",
    val groupTitle: String? = "",
    val groupSubTitle: String? = "",
    val groupImg: String? = "",
    val groupFavorite: Boolean? = false
): Parcelable
