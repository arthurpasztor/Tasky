package com.example.tasky.core.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class Preferences(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preferences.edit()

    private var encryptedPreferences: SharedPreferences = run {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            EncryptedSharedPreferences.create(
                ENCRYPTED_PREFERENCES_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

    fun containsEncrypted(key: String): Boolean {
        return encryptedPreferences.contains(key)
    }

    fun getString(key: String, default: String): String {
        return preferences.getString(key, default) ?: return default
    }

    fun putString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun isOfflineActivity(): Boolean {
        return preferences.getBoolean(KEY_IS_OFFLINE_ACTIVITY, false)
    }

    fun setOfflineActivity(value: Boolean) {
        editor.putBoolean(KEY_IS_OFFLINE_ACTIVITY, value)
        editor.apply()
    }

    fun removeAll() {
        editor.clear()
        editor.apply()
    }
    fun getEncryptedString(key: String, default: String): String {
        return encryptedPreferences.getString(key, default) ?: default
    }

    fun putEncryptedString(key: String, value: String) {
        encryptedPreferences.edit().putString(key, value).apply()
    }

    fun removeEncrypted(key: String) {
        encryptedPreferences.edit().remove(key).apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "TaskyStore"
        private const val ENCRYPTED_PREFERENCES_NAME = "EncryptedTaskyStore"

        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_USER_NAME = "full_name"
        const val KEY_USER_ID = "user_id"
        const val KEY_IS_OFFLINE_ACTIVITY = "is_offline_activity"
    }
}
