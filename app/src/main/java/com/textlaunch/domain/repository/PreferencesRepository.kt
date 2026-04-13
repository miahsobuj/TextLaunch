package com.textlaunch.domain.repository

import com.textlaunch.domain.model.LauncherSettings

interface PreferencesRepository {
    suspend fun getSettings(): LauncherSettings
    suspend fun saveSettings(settings: LauncherSettings)
    suspend fun getHomeApps(): List<String>
    suspend fun saveHomeApps(packageNames: List<String>)
}