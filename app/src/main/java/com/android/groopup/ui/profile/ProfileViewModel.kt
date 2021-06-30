package com.android.groopup.ui.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.groopup.data.remote.model.UserModel
import com.android.groopup.data.repository.ApiRepository
import com.android.groopup.utils.network.NetworkHelper
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileViewModel@ViewModelInject constructor(
    private val apiRepository: ApiRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val connectionError = "Check Your Internet Connection"

    init {

    }

    fun updateUser(userModel: UserModel){
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                apiRepository.createUser(userModel,userModel.userID!!).let { response ->
                    if (response.isSuccessful){
                        Timber.i("Update User Response Success")
                    }
                }
            }
        }
    }

}