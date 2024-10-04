package com.example.zametka

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class UserStore(
    val context: Context
) {

    companion object {
        val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(
            "userToken"
        )
        private val ZAMETKA = stringPreferencesKey("zametka")
        private val ZADACHA = stringPreferencesKey("zadacha")

    }


    var checkCount: Int by mutableStateOf(readcheckCount())


    private fun readcheckCount(): Int {
        return runBlocking {
            context.dataStore.data
                .map { preferences ->
                    preferences[intPreferencesKey("checkCount")] ?: 0
                }
                .first()
        }
    }


    suspend fun updatecheckCount(newcheckCount: Int) {
        context.dataStore.edit { preferences ->
            preferences[intPreferencesKey("checkCount")] = newcheckCount
        }
        checkCount =
            context.dataStore.data.first()[intPreferencesKey("checkCount")] ?: 0
    }


    val getAccessToken_2: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[ZADACHA]?.split(",") ?: emptyList()
    }

    suspend fun clearTasks() {
        context.dataStore.edit { preferences ->
            preferences.remove(ZADACHA)
        }
    }

    suspend fun saveToken_2(zadacha: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[ZADACHA] = zadacha.joinToString(",")
        }
    }


    val getAccessToken_1: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[ZAMETKA]?.split(".>") ?: emptyList()
    }

    suspend fun saveToken_1(zametki: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[ZAMETKA] = zametki.joinToString(".>")
        }
    }

    suspend fun clearToken_1() {
        context.dataStore.edit { preferences ->
            preferences.remove(ZAMETKA)
        }
    }


    var openThemeValue: Boolean by mutableStateOf(readOpenThemeValue())


    private fun readOpenThemeValue(): Boolean {
        return runBlocking {
            context.dataStore.data
                .map { preferences ->
                    preferences[booleanPreferencesKey("openThemeValue")] ?: false
                }
                .first()
        }
    }


    suspend fun updateOpenThemeValue(newValue: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("openThemeValue")] = newValue
        }
        openThemeValue =
            context.dataStore.data.first()[booleanPreferencesKey("openThemeValue")] ?: false
    }

    var isLoggedIn: Boolean by mutableStateOf(readisLoggedIn())


    private fun readisLoggedIn(): Boolean {
        return runBlocking {
            context.dataStore.data
                .map { preferences ->
                    preferences[booleanPreferencesKey("isLoggedIn")] ?: false
                }
                .first()
        }
    }


    suspend fun updateisLoggedIn(newValue: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("isLoggedIn")] = newValue
        }
        isLoggedIn =
            context.dataStore.data.first()[booleanPreferencesKey("isLoggedIn")] ?: false
    }
}





