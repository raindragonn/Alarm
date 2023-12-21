package com.bluepig.alarm.data.preference

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import com.bluepig.alarm.domain.preferences.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(
    @ApplicationContext private val _context: Context
) : AppPreferences {
    private val _pref by lazy { _context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE) }

    override var selectedAccountName: String?
        get() = _pref.getString(SELECTED_ACCOUNT_NAME_KEY, null)
        set(value) {
            _pref.edit {
                putString(SELECTED_ACCOUNT_NAME_KEY, value)
            }
        }

    companion object {
        private const val PREF_NAME = "APP_PREF"
        private const val SELECTED_ACCOUNT_NAME_KEY = "SELECTED_ACCOUNT_NAME_KEY"
    }
}