package com.textlaunch.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.textlaunch.R
import com.textlaunch.TextLaunchApp
import com.textlaunch.data.repository.AppRepositoryImpl
import com.textlaunch.data.repository.PreferencesRepositoryImpl
import com.textlaunch.domain.model.AppInfo
import com.textlaunch.domain.model.LauncherSettings
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var gridLayout: GridLayout
    private lateinit var appRepository: AppRepositoryImpl
    private lateinit var preferencesRepository: PreferencesRepositoryImpl

    private var currentSettings = LauncherSettings()
    private var homeApps = mutableListOf<AppInfo>()
    private var allApps = listOf<AppInfo>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as TextLaunchApp
        appRepository = AppRepositoryImpl(requireContext())
        preferencesRepository = PreferencesRepositoryImpl(requireContext())

        gridLayout = view.findViewById(R.id.home_grid)

        setupHeader(view)
        loadApps()
    }

    private fun setupHeader(view: View) {
        val settingsBtn = view.findViewById<TextView>(R.id.btn_settings)
        val appsBtn = view.findViewById<TextView>(R.id.btn_apps)

        settingsBtn.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(
                com.textlaunch.ui.settings.SettingsFragment()
            )
        }

        appsBtn.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(
                com.textlaunch.ui.apps.AppListFragment()
            )
        }
    }

    private fun loadApps() {
        lifecycleScope.launch {
            allApps = appRepository.getInstalledApps()
            val homePackageNames = preferencesRepository.getHomeApps()

            homeApps = if (homePackageNames.isEmpty()) {
                // Show first 20 apps by default
                allApps.take(20).toMutableList()
            } else {
                allApps.filter { it.packageName in homePackageNames }.toMutableList()
            }

            currentSettings = preferencesRepository.getSettings()
            applySettings()
            renderApps()
        }
    }

    private fun applySettings() {
        gridLayout.columnCount = currentSettings.gridColumns
        gridLayout.rowCount = currentSettings.gridRows
        gridLayout.setBackgroundColor(currentSettings.backgroundColor)
    }

    private fun renderApps() {
        gridLayout.removeAllViews()

        val cellWidth = resources.displayMetrics.widthPixels / currentSettings.gridColumns
        val cellHeight = resources.displayMetrics.heightPixels / currentSettings.gridRows

        homeApps.forEachIndexed { index, app ->
            val appView = createAppView(app, cellWidth, cellHeight)
            gridLayout.addView(appView)

            val row = index / currentSettings.gridColumns
            val col = index % currentSettings.gridColumns

            val params = GridLayout.spec(row, 1f)
            val colSpec = GridLayout.spec(col, 1f)
        }
    }

    private fun createAppView(app: AppInfo, width: Int, height: Int): View {
        return TextView(requireContext()).apply {
            text = app.appName
            setTextColor(currentSettings.textColor)
            textSize = currentSettings.fontSize
            typeface = Typeface.create(currentSettings.fontFamily, Typeface.NORMAL)
            gravity = android.view.Gravity.CENTER
            setPadding(8, 8, 8, 8)

            layoutParams = GridLayout.LayoutParams(width, height)

            setOnClickListener {
                appRepository.launchApp(app)
            }
        }
    }

    fun refreshApps() {
        loadApps()
    }
}