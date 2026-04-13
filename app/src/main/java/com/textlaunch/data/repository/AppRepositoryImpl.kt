package com.textlaunch.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.textlaunch.domain.model.AppInfo
import com.textlaunch.domain.repository.AppRepository

class AppRepositoryImpl(private val context: Context) : AppRepository {

    override fun getInstalledApps(): List<AppInfo> {
        val packageManager = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps = packageManager.queryIntentActivities(mainIntent, 0)
            .filter { it.activityInfo.packageName != context.packageName }
            .map { resolveInfo ->
                AppInfo(
                    packageName = resolveInfo.activityInfo.packageName,
                    appName = resolveInfo.loadLabel(packageManager).toString(),
                    activityName = resolveInfo.activityInfo.name
                )
            }
            .sortedBy { it.appName.lowercase() }

        return apps
    }

    override fun launchApp(appInfo: AppInfo) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setClassName(appInfo.packageName, appInfo.activityName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}