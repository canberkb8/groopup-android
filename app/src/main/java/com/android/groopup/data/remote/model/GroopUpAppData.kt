package com.android.groopup.data.remote.model

object GroopUpAppData {

    private var userList: ArrayList<UserModel> = arrayListOf()
    fun getUserList(): ArrayList<UserModel> { return userList }
    fun setUserList(userList: ArrayList<UserModel>) { this.userList = userList }
    fun updateUserForList(index: Int, userModel: UserModel) { userList[index] = userModel }
    fun addUserInUserList(userModel: UserModel) { userList.add(userModel)}

    private var currentUser: UserModel? = null
    fun getCurrentUser(): UserModel? { return currentUser }
    fun setCurrentUser(currentUser: UserModel?) { this.currentUser = currentUser }
}