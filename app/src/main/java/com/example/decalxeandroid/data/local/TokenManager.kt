package com.example.decalxeandroid.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tokens")

class TokenManager {
    private var context: Context? = null
    
    fun initialize(context: Context) {
        this.context = context
    }
    
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
    
    val accessToken: Flow<String?> = context?.dataStore?.data?.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    } ?: kotlinx.coroutines.flow.flowOf(null)
    
    val refreshToken: Flow<String?> = context?.dataStore?.data?.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    } ?: kotlinx.coroutines.flow.flowOf(null)
    
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context?.dataStore?.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }
    
    suspend fun getAccessToken(): String? {
        return context?.dataStore?.data?.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }?.first() ?: null
    }
    
    suspend fun getRefreshToken(): String? {
        return context?.dataStore?.data?.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }?.first() ?: null
    }
    
    suspend fun clearTokens() {
        context?.dataStore?.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }
}
