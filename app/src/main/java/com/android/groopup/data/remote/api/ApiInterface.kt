package com.android.groopup.data.remote.api

import com.android.groopup.data.remote.model.HomePageModel
import com.android.groopup.data.remote.model.UserModel
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET("userList/{userID}.json")
    suspend fun getUserData(@Path("userID") userID:String): Response<UserModel>

    @PUT("userList/{userID}.json")
    suspend fun createUser(@Body userModel: UserModel,@Path("userID") userID:String): Response<Void>

    @GET("groupList/{groupID}.json")
    suspend fun getGroupData(@Path("groupID") groupID:String): Response<HomePageModel>

    @PUT("groupList/{groupID}.json")
    suspend fun createGroup(@Body homePageModel: HomePageModel,@Path("groupID") groupID:String): Response<Void>

}