package com.example.zametka

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
        preferences[ZAMETKA]?.split(",") ?: emptyList()
    }

    suspend fun saveToken_1(zametki: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[ZAMETKA] = zametki.joinToString(" ")
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


    suspend fun updateOpenThemeValue(newValue: Boolean){
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("openThemeValue")] = newValue
        }
        openThemeValue =
            context.dataStore.data.first()[booleanPreferencesKey("openThemeValue")] ?: false
    }







}





