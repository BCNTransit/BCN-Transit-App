package com.bcntransit.app.data.enums

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.bcntransit.app.R

enum class BottomTab(@StringRes val labelResId: Int, val icon: ImageVector) {
    MAP(R.string.menu_map, Icons.Default.Map),
    SEARCH(R.string.menu_lines, Icons.Default.Route),
    FAVORITES(R.string.menu_favourites, Icons.Filled.Star),
    SETTINGS(R.string.menu_settings, Icons.Default.Settings)
}
