package com.android.groopup.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.groopup.data.remote.model.GroopUpAppData
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.data.repository.ApiRepository
import com.android.groopup.utils.network.NetworkHelper
import com.android.groopup.utils.network.Resource
import kotlinx.coroutines.launch

class SplashViewModel @ViewModelInject constructor(
    private val apiRepository: ApiRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val connectionError = "Check Your Internet Connection"

    private val userListData = MutableLiveData<Resource<HashMap<String,UserModel>>>()
    val userList: LiveData<Resource<HashMap<String,UserModel>>> get() = userListData

    init {
        getUserList()
    }

    private fun getUserList(){
        viewModelScope.launch {
            userListData.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                apiRepository.getUserList().let { response ->
                    if (response.isSuccessful){
                        userListData.postValue(Resource.success(response.body()))
                        GroopUpAppData.setUserList(generateHashToArray(response.body()!!))
                    }else{
                        userListData.postValue(Resource.error(response.errorBody().toString(),null))
                    }
                }
            }else{
                userListData.postValue(Resource.error(connectionError,null))
            }
        }
    }

    private fun generateHashToArray(hashMap:HashMap<String,UserModel>):ArrayList<UserModel>{
        val list:ArrayList<UserModel> = arrayListOf()
        for(item in hashMap){
            list.add(item.value)
        }
        return list
    }
}