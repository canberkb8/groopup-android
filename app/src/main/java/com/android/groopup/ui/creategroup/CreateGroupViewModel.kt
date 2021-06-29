package com.android.groopup.ui.creategroup

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.android.groopup.data.remote.model.GroopUpAppData
import com.android.groopup.data.remote.model.GroupModel
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.data.repository.ApiRepository
import com.android.groopup.utils.network.NetworkHelper
import com.android.groopup.utils.network.Resource
import kotlinx.coroutines.launch
import timber.log.Timber

class CreateGroupViewModel @ViewModelInject constructor(
    private val apiRepository: ApiRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val connectionError = "Check Your Internet Connection"
    private val createGroupData = MutableLiveData<Resource<Void>>()
    val createGroup: LiveData<Resource<Void>> get() = createGroupData

    private val searchUserData = MutableLiveData<Resource<UserModel>>()
    val searchUser: LiveData<Resource<UserModel>> get() = searchUserData

    init {

    }

    fun createGroup(groupModel: GroupModel){
        viewModelScope.launch {
            createGroupData.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                apiRepository.createGroup(groupModel,groupModel.groupID!!).let { response ->
                    if (response.isSuccessful){
                        createGroupData.postValue(Resource.success(response.body()))
                    }else{
                        createGroupData.postValue(Resource.error(response.errorBody().toString(),null))
                    }
                }
            }else{
                createGroupData.postValue(Resource.error(connectionError,null))
            }
        }
    }

    fun searchUser(email:String){
        viewModelScope.launch {
            searchUserData.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                apiRepository.getUserList().let { response ->
                    if (response.isSuccessful){
                        searchUserData.postValue(Resource.success(findUserWithEmail(email,generateHashToArray(response.body()!!))))
                    }else{
                        searchUserData.postValue(Resource.error(response.errorBody().toString(),null))
                    }
                }
            }else{
                searchUserData.postValue(Resource.error(connectionError,null))
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

    private fun findUserWithEmail(email:String,list:ArrayList<UserModel>):UserModel?{
        for (item in list){
            if (item.userEmail == email){
                return item
            }
        }
        return null
    }

    fun generateGroupID():String{
        return  java.util.UUID.randomUUID().toString() + java.util.Calendar.getInstance().timeInMillis
    }
}