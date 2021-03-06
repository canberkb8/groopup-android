package com.android.groopup.ui.homepage

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.android.groopup.data.remote.model.GroupModel
import com.android.groopup.data.remote.model.UserGroupModel
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.data.repository.ApiRepository
import com.android.groopup.utils.network.NetworkHelper
import com.android.groopup.utils.network.Resource
import kotlinx.coroutines.launch
import timber.log.Timber

class HomePageViewModel@ViewModelInject constructor(
    private val apiRepository: ApiRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val connectionError = "Check Your Internet Connection"
    private val groupData = MutableLiveData<Resource<GroupModel>>()
    val group: LiveData<Resource<GroupModel>> get() = groupData

    private val userData = MutableLiveData<Resource<UserModel>>()
    val user: LiveData<Resource<UserModel>> get() = userData

    init {

    }

    fun getUserData(userID:String){
        viewModelScope.launch {
            userData.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                apiRepository.getUserData(userID).let { response ->
                    if (response.isSuccessful){
                        userData.postValue(Resource.success(response.body()))
                    }else{
                        userData.postValue(Resource.error(response.errorBody().toString(),null))
                    }
                }
            }else{
                userData.postValue(Resource.error(connectionError,null))
            }
        }
    }

    fun getGroupData(groupID:String){
        viewModelScope.launch {
            groupData.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                apiRepository.getGroupData(groupID).let { response ->
                    if (response.isSuccessful){
                        groupData.postValue(Resource.success(response.body()))
                    }else{
                        groupData.postValue(Resource.error(response.errorBody().toString(),null))
                    }
                }
            }else{
                groupData.postValue(Resource.error(connectionError,null))
            }
        }
    }

    fun generateHashToArray(hashMap:HashMap<String,UserGroupModel>):ArrayList<UserGroupModel>{
        val list:ArrayList<UserGroupModel> = arrayListOf()
        for(item in hashMap){
            list.add(item.value)
        }
        return list
    }
}