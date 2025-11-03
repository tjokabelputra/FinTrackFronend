package com.rpl.fintrack.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rpl.fintrack.database.AccountRepository
import com.rpl.fintrack.di.Injection
import com.rpl.fintrack.ui.login.LoginViewModel
import com.rpl.fintrack.ui.signup.SignupViewModel

class AccountModelFactory private constructor(private val accountRepository: AccountRepository): ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SignupViewModel::class.java)){
            return SignupViewModel(accountRepository) as T
        }
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(accountRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object{
        @Volatile
        private var instance: AccountModelFactory? = null
        fun getInstance(): AccountModelFactory =
            instance ?: synchronized(this){
                instance ?: AccountModelFactory(Injection.provideAccountRepository())
            }.also { instance = it }
    }
}