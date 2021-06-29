package com.android.groopup.data.repository

import com.android.groopup.data.remote.api.ApiInterface
import com.android.groopup.data.remote.model.GroupModel
import com.android.groopup.data.remote.model.UserModel
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiInterface: ApiInterface) {

    /** User Services*/
    suspend fun createUser(userModel: UserModel, userID: String) = apiInterface.createUser(userModel = userModel, userID = userID)
    suspend fun getUserData(userID: String) = apiInterface.getUserData(userID = userID)
    suspend fun getUserList() = apiInterface.getUserList()

    /** Group Services*/
    suspend fun getGroupData(groupID: String) = apiInterface.getGroupData(groupID = groupID)
    suspend fun createGroup(groupModel: GroupModel, groupID: String) = apiInterface.createGroup(groupModel = groupModel, groupID = groupID)
}