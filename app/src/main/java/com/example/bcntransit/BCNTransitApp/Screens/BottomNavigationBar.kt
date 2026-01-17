package com.bcntransit.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bcntransit.app.R
import com.bcntransit.app.data.enums.BottomTab
import androidx.compose.ui.res.stringResource

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    val navBarShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

    NavigationBar(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = navBarShape)
            .clip(navBarShape),
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        BottomTab.values().forEach { tab ->
            val isSelected = tab == selectedTab

            val iconColor: Color = if (isSelected) colorResource(R.color.red)
            else MaterialTheme.colorScheme.onSurfaceVariant
            val iconSize: Dp = if (isSelected) 36.dp else 24.dp

            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier.size(iconSize),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(tab.labelResId),
                            tint = iconColor
                        )
                    }
                },
                label = {
                    Text(
                        text = stringResource(tab.labelResId),
                        color = iconColor
                    )
                },
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = colorResource(R.color.low_red)
                )
            )
        }
    }
}