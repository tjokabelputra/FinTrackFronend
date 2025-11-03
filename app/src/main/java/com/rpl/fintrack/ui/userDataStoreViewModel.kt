package com.rpl.fintrack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpl.fintrack.database.User
import com.rpl.fintrack.database.userPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class userDataStoreViewModel(private val pref: userPreference) : ViewModel() {
    fun saveUser(uid: String, token: String){
        viewModelScope.launch {
            val user = User(uid, token)
            pref.saveUser(user)
        }
    }

    fun deleteUser(){
        viewModelScope.launch {
            pref.clearUser()
        }
    }

    fun getUid(): Flow<String> = pref.getUid()
}