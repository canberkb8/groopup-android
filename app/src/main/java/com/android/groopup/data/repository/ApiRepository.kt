package com.android.groopup.data.repository

import com.android.groopup.data.remote.api.ApiInterface
import com.android.groopup.data.remote.model.HomePageModel
import com.android.groopup.data.remote.model.UserModel
import retrofit2.http.Path
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiInterface: ApiInterface) {

    /** User Services*/
    suspend fun createUser(userModel: UserModel, userID: String) = apiInterface.createUser(userModel = userModel, userID = userID)
    suspend fun getUserData(userID: String) = apiInterface.getUserData(userID = userID)

    /** Group Services*/
    suspend fun getGroupData(groupID: String) = apiInterface.getGroupData(groupID = groupID)
    suspend fun createGroup(homePageModel: HomePageModel, groupID: String) = apiInterface.createGroup(homePageModel = homePageModel, groupID = groupID)
}