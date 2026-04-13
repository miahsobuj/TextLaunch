package com.textlaunch.ui.settings

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.textlaunch.R
import com.textlaunch.data.repository.PreferencesRepositoryImpl
import com.textlaunch.domain.model.LauncherSettings
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var preferencesRepository: PreferencesRepositoryImpl
    private var currentSettings = LauncherSettings()

    private lateinit var colorPreview: View
    private lateinit var settingsContainer: LinearLayout

    private val neonColors = listOf(
        0xFF00FFFF.toInt(), // Cyan
        0xFFFF00FF.toInt(), // Magenta
        0xFF00FF00.toInt(), // Lime
        0xFFFFFF00.toInt(), // Yellow
        0xFFFF8800.toInt(), // Orange
        0xFFFF0088.toInt(), // Pink
        0xFF8800FF.toInt(), // Purple
        0xFFFF0044.toInt()  // Red
    )

    private val fontFamilies = listOf("monospace", "sans-serif", "serif")
    private val fontNames = listOf("Monospace", "Sans Serif", "Serif")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesRepository = PreferencesRepositoryImpl(requireContext())

        colorPreview = view.findViewById(R.id.color_preview)
        settingsContainer = view.findViewById(R.id.settings_container)

        val backButton = view.findViewById<TextView>(R.id.back_button)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        loadSettings()
    }

    private fun loadSettings() {
        lifecycleScope.launch {
            currentSettings = preferencesRepository.getSettings()
            buildSettingsUI()
        }
    }

    private fun buildSettingsUI() {
        settingsContainer.removeAllViews()

        // Text Color Section
        addSectionTitle("TEXT COLOR")
        addColorPicker()

        // Font Section
        addSectionTitle("FONT FAMILY")
        addFontPicker()

        // Font Size Section
        addSectionTitle("FONT SIZE")
        addFontSizePicker()

        // Grid Size Section
        addSectionTitle("GRID SIZE")
        addGridSizePicker()

        // Save Button
        addSaveButton()
    }

    private fun addSectionTitle(title: String) {
        val titleView = TextView(requireContext()).apply {
            text = title
            setTextColor(0xFF888888.toInt())
            textSize = 12f
            typeface = Typeface.MONOSPACE
            setPadding(0, 24, 0, 8)
        }
        settingsContainer.addView(titleView)
    }

    private fun addColorPicker() {
        val colorRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 8, 0, 16)
        }

        neonColors.forEach { color ->
            val colorBtn = View(requireContext()).apply {
                setBackgroundColor(color)
                setOnClickListener {
                    currentSettings = currentSettings.copy(textColor = color)
                    updateColorPreview()
                }
            }
            colorBtn.layoutParams = LinearLayout.LayoutParams(48, 48).apply {
                setMargins(4, 4, 4, 4)
            }
            colorRow.addView(colorBtn)
        }

        settingsContainer.addView(colorRow)
        updateColorPreview()
    }

    private fun updateColorPreview() {
        colorPreview.setBackgroundColor(currentSettings.textColor)
    }

    private fun addFontPicker() {
        val fontRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 8, 0, 16)
        }

        fontFamilies.forEachIndexed { index, font ->
            val fontBtn = TextView(requireContext()).apply {
                text = fontNames[index]
                setTextColor(
                    if (currentSettings.fontFamily == font) 0xFF00FF00.toInt()
                    else 0xFF888888.toInt()
                )
                textSize = 14f
                typeface = Typeface.create(font, Typeface.NORMAL)
                setPadding(16, 8, 16, 8)
                setOnClickListener {
                    currentSettings = currentSettings.copy(fontFamily = font)
                    buildSettingsUI()
                }
            }
            fontRow.addView(fontBtn)
        }

        settingsContainer.addView(fontRow)
    }

    private fun addFontSizePicker() {
        val sizeRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 8, 0, 16)
        }

        listOf(12f, 14f, 16f, 18f, 20f).forEach { size ->
            val sizeBtn = TextView(requireContext()).apply {
                text = "${size.toInt()}"
                setTextColor(
                    if (currentSettings.fontSize == size) 0xFF00FF00.toInt()
                    else 0xFF888888.toInt()
                )
                textSize = 14f
                typeface = Typeface.MONOSPACE
                setPadding(16, 8, 16, 8)
                setOnClickListener {
                    currentSettings = currentSettings.copy(fontSize = size)
                    buildSettingsUI()
                }
            }
            sizeRow.addView(sizeBtn)
        }

        settingsContainer.addView(sizeRow)
    }

    private fun addGridSizePicker() {
        val gridRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 8, 0, 16)
        }

        listOf(Pair(3, 4), Pair(4, 5), Pair(5, 6)).forEach { (cols, rows) ->
            val gridBtn = TextView(requireContext()).apply {
                text = "${cols}x${rows}"
                setTextColor(
                    if (currentSettings.gridColumns == cols && currentSettings.gridRows == rows)
                        0xFF00FF00.toInt()
                    else
                        0xFF888888.toInt()
                )
                textSize = 14f
                typeface = Typeface.MONOSPACE
                setPadding(16, 8, 16, 8)
                setOnClickListener {
                    currentSettings = currentSettings.copy(gridColumns = cols, gridRows = rows)
                    buildSettingsUI()
                }
            }
            gridRow.addView(gridBtn)
        }

        settingsContainer.addView(gridRow)
    }

    private fun addSaveButton() {
        val saveBtn = TextView(requireContext()).apply {
            text = "[ SAVE ]"
            setTextColor(0xFF00FF00.toInt())
            textSize = 16f
            typeface = Typeface.MONOSPACE
            setPadding(0, 32, 0, 32)
            gravity = android.view.Gravity.CENTER

            setOnClickListener {
                lifecycleScope.launch {
                    preferencesRepository.saveSettings(currentSettings)
                    parentFragmentManager.popBackStack()
                }
            }
        }
        settingsContainer.addView(saveBtn)
    }
}