package com.android.groopup.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.data.repository.ApiRepository
import com.android.groopup.utils.network.NetworkHelper
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel @ViewModelInject constructor(
    private val apiRepository: ApiRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val connectionError = "Check Your Internet Connection"

    init {

    }

    fun createUser(userModel: UserModel, userID:String){
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                apiRepository.createUser(userModel,userID).let { response ->
                    if (response.isSuccessful){
                        Timber.i("Create User Response Success")
                    }
                }
            }
        }
    }
}