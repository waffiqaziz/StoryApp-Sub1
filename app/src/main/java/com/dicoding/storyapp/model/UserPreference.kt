package com.dicoding.picodiploma.loginwithanimation.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

  fun getUser(): Flow<UserModel> {
    return dataStore.data.map {
      UserModel(
        it[NAME_KEY] ?: "",
        it[EMAIL_KEY] ?: "",
        it[PASSWORD_KEY] ?: "",
        it[STATE_KEY] ?: false
      )
    }
  }

  suspend fun saveUser(user: UserModel) {
    dataStore.edit {
      it[NAME_KEY] = user.name
      it[EMAIL_KEY] = user.email
      it[PASSWORD_KEY] = user.password
      it[STATE_KEY] = user.isLogin
    }
  }

  suspend fun login() {
    dataStore.edit {
      it[STATE_KEY] = true
    }
  }

  suspend fun logout() {
    dataStore.edit {
      it[STATE_KEY] = false
    }
  }

  companion object {
    @Volatile
    private var INSTANCE: UserPreference? = null

    private val NAME_KEY = stringPreferencesKey("name")
    private val EMAIL_KEY = stringPreferencesKey("email")
    private val PASSWORD_KEY = stringPreferencesKey("password")
    private val STATE_KEY = booleanPreferencesKey("state")

    fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
      return INSTANCE ?: synchronized(this) {
        val instance = UserPreference(dataStore)
        INSTANCE = instance
        instance
      }
    }
  }
}