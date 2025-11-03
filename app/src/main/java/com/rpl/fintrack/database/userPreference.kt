package com.rpl.fintrack.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow


val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class User(val uid: String, val token: String)

class userPreference private constructor(private val dataStore: DataStore<Preferences>){
    private val uid = stringPreferencesKey("uid")
    private val token = stringPreferencesKey("token")

    fun getUser(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                uid = preferences[uid] ?: "",
                token = preferences[token] ?: ""
            )
        }
    }

    fun getUid(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[uid] ?: ""
        }
    }

    suspend fun saveUser(user: User){
        dataStore.edit { preferences ->
            preferences[uid] = user.uid
            preferences[token] = user.token
        }
    }

    suspend fun clearUser(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: userPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): userPreference {
            return INSTANCE ?: synchronized(this){
                val instance = userPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}