package com.bcntransit.app.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcntransit.app.R
import com.bcntransit.app.util.LanguageManager
import com.bcntransit.app.util.getAndroidId
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
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context, getAndroidId(context))
    )
    val state by viewModel.state.collectAsState()

    var isRestarting by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    val currentLangCode = remember { LanguageManager.getSavedLanguage(context) }

    // Función para copiar el ID al portapapeles (detalle pro)
    fun copyIdToClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Device ID", getAndroidId(context))
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "ID copiado", Toast.LENGTH_SHORT).show()
    }

    val currentLanguageName = when (currentLangCode) {
        "es" -> "Español"
        "ca" -> "Català"
        "en" -> "English"
        else -> "Español"
    }

    if (isRestarting) {
        RestartLoadingDialog()
    }

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
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
                        (context as? android.app.Activity)?.recreate()
                    }
                }
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

            // --- SECCIÓN 1: GENERAL ---
            SettingsSectionHeader(title = stringResource(R.string.settings_notifications))

            SettingsSwitchItem(
                icon = Icons.Outlined.Notifications,
                title = stringResource(R.string.settings_receive_alerts),
                subtitle = stringResource(R.string.settings_push_notifications),
                checked = state.receiveAlerts,
                onCheckedChange = { viewModel.toggleReceiveAlerts(it) }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 56.dp)) // Indentado estilo Material

            // --- SECCIÓN 2: PREFERENCIAS ---
            SettingsSectionHeader(title = stringResource(R.string.settings_preferences))

            SettingsNavigationItem(
                icon = Icons.Outlined.Language,
                title = stringResource(R.string.settings_language),
                subtitle = currentLanguageName,
                onClick = { showLanguageDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))

            // --- SECCIÓN 3: INFORMACIÓN ---
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

            // --- FOOTER (ID & Versión) ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ID Copiable con estilo de "Badge"
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
                            text = getAndroidId(context),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace // Fuente técnica
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.version),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ------------------------------------------
// COMPONENTES REUTILIZABLES MEJORADOS
// ------------------------------------------

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
                modifier = Modifier.size(16.dp), // Chevron más sutil
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
        title = { Text(text = stringResource(R.string.settings_language)) }, // Usar recurso traducido
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
                Text(stringResource(android.R.string.cancel)) // Botón estándar de Android
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
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)), // Fondo del tema
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