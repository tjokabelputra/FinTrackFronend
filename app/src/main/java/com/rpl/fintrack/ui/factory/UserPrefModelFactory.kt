package com.rpl.fintrack.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rpl.fintrack.database.userPreference
import com.rpl.fintrack.di.Injection
import com.rpl.fintrack.ui.userDataStoreViewModel

class UserPrefModelFactory(private val userPreference: userPreference) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(userDataStoreViewModel::class.java)){
            return userDataStoreViewModel(userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object{
        @Volatile
        private var instance: UserPrefModelFactory? = null
        fun getInstance(context: Context): UserPrefModelFactory =
            instance ?: synchronized(this){
                instance ?: UserPrefModelFactory(Injection.provideAccountDataStore(context.applicationContext))
            }.also { instance = it }
    }
}