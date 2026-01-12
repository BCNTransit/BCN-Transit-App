package com.bcntransit.app.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline // Asegúrate de tener acceso a los iconos extendidos o usa filled con tint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bcntransit.app.R
import com.bcntransit.app.model.FavoriteDto
import com.bcntransit.app.api.ApiClient
import com.bcntransit.app.util.getUserId
import com.example.bcntransit.BCNTransitApp.Screens.favorites.FavoriteItem
import com.example.bcntransit.BCNTransitApp.components.CustomTopBar
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    onFavoriteSelected: (FavoriteDto) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var userId = getUserId()

    var favorites by remember { mutableStateOf<List<FavoriteDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val favoriteDeleted = stringResource(R.string.favorite_deleted)
    val connectionError = stringResource(R.string.connection_error)
    val deleteFavoriteError = stringResource(R.string.delete_favorite_error)

    LaunchedEffect(userId) {
        loading = true
        error = null
        try {
            favorites = ApiClient.userApiService.getUserFavorites()
        } catch (e: Exception) {
            e.printStackTrace()
            error = e.message
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            text = stringResource(R.string.favorites),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                },
                onBackClick = { },
                showBackButton = false
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            when {
                loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorResource(R.color.medium_red))
                    }
                }
                error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
                    }
                }
                favorites.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = stringResource(R.string.favorites_empty_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.favorites_empty_body),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // --- ESTADO NORMAL: LISTA CON DATOS ---
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.favorites_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        val groupedFavorites = favorites.groupBy { it.TYPE }

                        groupedFavorites.forEach { (type, favoritesOfType) ->
                            item {
                                Text(
                                    text = type.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            itemsIndexed(
                                items = favoritesOfType,
                                key = { _, fav -> "${fav.TYPE}_${fav.STATION_CODE}" }
                            ) { _, fav ->
                                FavoriteItem(
                                    fav = fav,
                                    onClick = { onFavoriteSelected(fav) },
                                    onDelete = {
                                        coroutineScope.launch {
                                            try {
                                                loading = true

                                                val deleted = ApiClient.userApiService.deleteUserFavorite(
                                                    fav.TYPE,
                                                    fav.STATION_CODE
                                                )

                                                if (deleted) {
                                                    favorites = ApiClient.userApiService.getUserFavorites()
                                                    snackbarHostState.showSnackbar(favoriteDeleted)
                                                } else {
                                                    snackbarHostState.showSnackbar(deleteFavoriteError)
                                                }
                                            } catch (e: Exception) {
                                                snackbarHostState.showSnackbar(connectionError)
                                            } finally {
                                                loading = false
                                            }
                                        }
                                    }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}