package com.bcntransit.app.screens.settings

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SettingsSystemDaydream
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcntransit.app.R
import com.bcntransit.app.ui.theme.AppThemeMode
import com.bcntransit.app.util.LanguageManager
import com.bcntransit.app.util.getUserId
import com.example.bcntransit.BCNTransitApp.Screens.settings.SettingsViewModel
import com.example.bcntransit.BCNTransitApp.Screens.settings.SettingsViewModelFactory
import com.example.bcntransit.BCNTransitApp.components.CustomTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTermsAndConditions: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val idCopied = stringResource(R.string.id_copied)
    val userId = getUserId()

    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )
    val state by viewModel.state.collectAsState()

    var isRestarting by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    val currentLangCode = remember { LanguageManager.getSavedLanguage(context) }

    fun copyIdToClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Device ID", userId)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, idCopied, Toast.LENGTH_SHORT).show()
    }

    val currentLanguageName = when (currentLangCode) {
        "es" -> "Español"
        "ca" -> "Català"
        "en" -> "English"
        else -> "Español"
    }

    // Texto para mostrar la selección actual
    val currentThemeName = when (state.themeMode) {
        AppThemeMode.LIGHT -> stringResource(R.string.theme_light)
        AppThemeMode.DARK -> stringResource(R.string.theme_dark)
        else -> stringResource(R.string.theme_light)
    }

    if (isRestarting) {
        RestartLoadingDialog()
    }

    // Diálogo de Idioma
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguageCode = currentLangCode,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { code ->
                showLanguageDialog = false
                scope.launch {
                    isRestarting = true
                    delay(500)
                    LanguageManager.setLocale(context, code)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        (context as? Activity)?.recreate()
                    }
                }
            }
        )
    }

    // Nuevo Diálogo de Tema
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = state.themeMode,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { newTheme ->
                showThemeDialog = false
                viewModel.setThemeMode(newTheme)
            }
        )
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = { Text(stringResource(R.string.menu_settings)) },
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            SettingsSectionHeader(title = stringResource(R.string.settings_notifications))

            SettingsSwitchItem(
                icon = Icons.Outlined.Notifications,
                title = stringResource(R.string.settings_receive_alerts),
                subtitle = stringResource(R.string.settings_push_notifications),
                checked = state.receiveAlerts,
                onCheckedChange = { viewModel.toggleReceiveAlerts(it) }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))

            SettingsSectionHeader(title = stringResource(R.string.settings_preferences))

            SettingsNavigationItem(
                icon = Icons.Outlined.Language,
                title = stringResource(R.string.settings_language),
                subtitle = currentLanguageName,
                onClick = { showLanguageDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))

            SettingsNavigationItem(
                icon = Icons.Outlined.Palette,
                title = stringResource(R.string.settings_theme),
                subtitle = currentThemeName,
                onClick = { showThemeDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))

            SettingsSectionHeader(title = stringResource(R.string.settings_information))

            SettingsNavigationItem(
                icon = Icons.Outlined.Info,
                title = stringResource(R.string.settings_about),
                onClick = onNavigateToAbout
            )

            SettingsNavigationItem(
                icon = Icons.Outlined.Security,
                title = stringResource(R.string.settings_privacy),
                onClick = onNavigateToPrivacy
            )

            SettingsNavigationItem(
                icon = Icons.Outlined.Description,
                title = stringResource(R.string.settings_terms_and_conditions),
                onClick = onNavigateToTermsAndConditions
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    onClick = { copyIdToClipboard() },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.settings_id).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = userId,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.version),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: AppThemeMode,
    onDismiss: () -> Unit,
    onThemeSelected: (AppThemeMode) -> Unit
) {
    val options = listOf(
        Triple(AppThemeMode.LIGHT, stringResource(R.string.theme_light), Icons.Outlined.LightMode),
        Triple(AppThemeMode.DARK, stringResource(R.string.theme_dark), Icons.Outlined.DarkMode),
        Triple(AppThemeMode.SYSTEM, stringResource(R.string.theme_system), Icons.Outlined.SettingsSystemDaydream)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.settings_theme_select)) },
        text = {
            Column {
                options.forEach { (mode, name, icon) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (mode == currentTheme),
                                onClick = { onThemeSelected(mode) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (mode == currentTheme),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsNavigationItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        headlineContent = {
            Text(title, style = MaterialTheme.typography.bodyLarge)
        },
        supportingContent = if (subtitle != null) {
            { Text(subtitle) }
        } else null,
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onCheckedChange(!checked) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        headlineContent = {
            Text(title, style = MaterialTheme.typography.bodyLarge)
        },
        supportingContent = if (subtitle != null) {
            { Text(subtitle) }
        } else null,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

@Composable
fun LanguageSelectionDialog(
    currentLanguageCode: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf("es" to "Español", "ca" to "Català", "en" to "English")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.settings_language)) },
        text = {
            Column {
                languages.forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (code == currentLanguageCode),
                                onClick = { onLanguageSelected(code) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (code == currentLanguageCode),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}

@Composable
fun RestartLoadingDialog() {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.settings_updating_language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}