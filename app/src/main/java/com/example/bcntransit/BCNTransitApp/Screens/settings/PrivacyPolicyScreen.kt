package com.example.bcntransit.BCNTransitApp.Screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Announcement
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bcntransit.app.R
import com.example.bcntransit.BCNTransitApp.components.CustomTopBar
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    Scaffold(
        topBar = {
            CustomTopBar(
                title = { Text(stringResource(R.string.settings_privacy)) }, // Usa tus recursos
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Icon(
                imageVector = Icons.Outlined.Security,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Política de privacidad",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Badge de fecha
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = "Última actualización: 27 oct. 2025",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Introducción destacada
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "BCNTransit respeta tu privacidad y se compromete a proteger tus datos personales. Esta política describe qué información recopilamos, cómo la usamos y qué derechos tienes.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- SECCIONES LEGALES ---

            PolicySection(
                icon = Icons.Outlined.FolderShared,
                title = "1. Información que recopilamos",
                body = """
                • Identificador del dispositivo (Android ID), usado de forma anónima para mejorar la estabilidad.
                • Preferencias de configuración (tema oscuro, notificaciones).
                • Datos agregados de uso (frecuencia, errores).
                
                BCNTransit no recopila datos sensibles, ubicaciones en tiempo real ni información de contacto.
                """.trimIndent()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            PolicySection(
                icon = Icons.Outlined.CloudSync,
                title = "2. Uso de la información",
                body = """
                Los datos se utilizan exclusivamente para:
                • Mostrar información del transporte en tiempo real.
                • Enviar notificaciones de incidencias.
                • Mejorar el rendimiento de la app.
                """.trimIndent()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            PolicySection(
                icon = Icons.Outlined.Announcement,
                title = "3. Compartición de datos",
                body = "BCNTransit no comparte ni vende información personal a terceros. Algunos datos técnicos pueden ser procesados por servicios como Firebase Cloud Messaging para notificaciones, bajo sus propias políticas."
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            PolicySection(
                icon = Icons.Outlined.Lock,
                title = "4. Seguridad",
                body = "Implementamos medidas técnicas y organizativas para proteger tus datos frente a accesos no autorizados, pérdida o alteración."
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            PolicySection(
                icon = Icons.Outlined.Gavel,
                title = "5. Tus derechos",
                body = "Puedes solicitar la eliminación de tus datos locales desinstalando la aplicación. BCNTransit no almacena datos identificables en servidores externos."
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            PolicySection(
                icon = Icons.Outlined.Update,
                title = "6. Cambios en esta política",
                body = "Esta política puede actualizarse. Te notificaremos cambios importantes mediante la app o el sitio web oficial."
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Footer
            Text(
                text = "© $currentYear BCNTransit. Barcelona.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        }
    }
}

// COMPONENTE AUXILIAR PARA SECCIONES LIMPIAS
@Composable
private fun PolicySection(
    icon: ImageVector,
    title: String,
    body: String
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Icono lateral alineado al título
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp) // Ajuste visual fino
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp, // Mejor lectura
                    letterSpacing = 0.15.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start // Justify a veces crea ríos blancos feos en móviles
            )
        }
    }
}