package com.textlaunch.domain.repository

import com.textlaunch.domain.model.AppInfo

interface AppRepository {
    fun getInstalledApps(): List<AppInfo>
    fun launchApp(appInfo: AppInfo)
}