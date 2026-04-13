package com.textlaunch.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.textlaunch.domain.model.LauncherSettings
import com.textlaunch.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "textlaunch_settings")

class PreferencesRepositoryImpl(private val context: Context) : PreferencesRepository {

    companion object {
        private val TEXT_COLOR = intPreferencesKey("text_color")
        private val FONT_FAMILY = stringPreferencesKey("font_family")
        private val FONT_SIZE = floatPreferencesKey("font_size")
        private val GRID_COLUMNS = intPreferencesKey("grid_columns")
        private val GRID_ROWS = intPreferencesKey("grid_rows")
        private val BACKGROUND_COLOR = intPreferencesKey("background_color")
        private val HOME_APPS = stringSetPreferencesKey("home_apps")
    }

    override suspend fun getSettings(): LauncherSettings {
        return context.dataStore.data.map { preferences ->
            LauncherSettings(
                textColor = preferences[TEXT_COLOR] ?: 0xFF00FFFF.toInt(),
                fontFamily = preferences[FONT_FAMILY] ?: "monospace",
                fontSize = preferences[FONT_SIZE] ?: 14f,
                gridColumns = preferences[GRID_COLUMNS] ?: 4,
                gridRows = preferences[GRID_ROWS] ?: 5,
                backgroundColor = preferences[BACKGROUND_COLOR] ?: 0xFF0D0D0D.toInt()
            )
        }.first()
    }

    override suspend fun saveSettings(settings: LauncherSettings) {
        context.dataStore.edit { preferences ->
            preferences[TEXT_COLOR] = settings.textColor
            preferences[FONT_FAMILY] = settings.fontFamily
            preferences[FONT_SIZE] = settings.fontSize
            preferences[GRID_COLUMNS] = settings.gridColumns
            preferences[GRID_ROWS] = settings.gridRows
            preferences[BACKGROUND_COLOR] = settings.backgroundColor
        }
    }

    override suspend fun getHomeApps(): List<String> {
        return context.dataStore.data.map { preferences ->
            preferences[HOME_APPS]?.toList() ?: emptyList()
        }.first()
    }

    override suspend fun saveHomeApps(packageNames: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[HOME_APPS] = packageNames.toSet()
        }
    }
}