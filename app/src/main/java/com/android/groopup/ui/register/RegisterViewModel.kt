package com.android.groopup.ui.register

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.data.repository.ApiRepository
import com.android.groopup.utils.network.NetworkHelper
import com.android.groopup.utils.network.Resource
import kotlinx.coroutines.launch
import timber.log.Timber

class RegisterViewModel @ViewModelInject constructor(
    private val apiRepository: ApiRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val connectionError = "Check Your Internet Connection"
    private val createUserData = MutableLiveData<Resource<Void>>()
    val createUser: LiveData<Resource<Void>> get() = createUserData
    init {

    }

    fun createUser(userModel: UserModel){
        viewModelScope.launch {
            createUserData.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                apiRepository.createUser(userModel,userModel.userID!!).let { response ->
                    if (response.isSuccessful){
                        createUserData.postValue(Resource.success(response.body()))
                        Timber.i("Create User Response Success")
                    }else{
                        createUserData.postValue(Resource.error(response.errorBody().toString(),null))
                    }
                }
            }else{
                createUserData.postValue(Resource.error(connectionError,null))
            }
        }
    }
}