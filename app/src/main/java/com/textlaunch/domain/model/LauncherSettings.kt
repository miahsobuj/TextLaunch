package com.textlaunch.domain.model

data class LauncherSettings(
    val textColor: Int = 0xFF00FFFF.toInt(),
    val fontFamily: String = "monospace",
    val fontSize: Float = 14f,
    val gridColumns: Int = 4,
    val gridRows: Int = 5,
    val backgroundColor: Int = 0xFF0D0D0D.toInt()
)