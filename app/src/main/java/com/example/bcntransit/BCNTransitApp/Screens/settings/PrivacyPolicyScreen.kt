package com.example.bcntransit.BCNTransitApp.Screens.settings

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
                title = { Text(stringResource(R.string.settings_privacy)) },
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
                text = stringResource(R.string.settings_privacy),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.last_update),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.privacy_intro),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            PolicySection(
                icon = Icons.Outlined.FolderShared,
                title = stringResource(R.string.privacy_section1_title),
                body = stringResource(R.string.privacy_section1_body)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            PolicySection(
                icon = Icons.Outlined.CloudSync,
                title = stringResource(R.string.privacy_section2_title),
                body = stringResource(R.string.privacy_section2_body)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            PolicySection(
                icon = Icons.Outlined.Announcement,
                title = stringResource(R.string.privacy_section3_title),
                body = stringResource(R.string.privacy_section3_body)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            PolicySection(
                icon = Icons.Outlined.Lock,
                title = stringResource(R.string.privacy_section4_title),
                body = stringResource(R.string.privacy_section4_body)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            PolicySection(
                icon = Icons.Outlined.Gavel,
                title = stringResource(R.string.privacy_section5_title),
                body = stringResource(R.string.privacy_section5_body)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            PolicySection(
                icon = Icons.Outlined.Update,
                title = stringResource(R.string.privacy_section6_title),
                body = stringResource(R.string.privacy_section6_body)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "© $currentYear BCNTransit. Barcelona.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PolicySection(
    icon: ImageVector,
    title: String,
    body: String
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp)
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
                    lineHeight = 22.sp,
                    letterSpacing = 0.15.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }
    }
}