package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.DesignStudioScreen
import com.example.ui.screens.FabricCostScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MeasurementScreen
import com.example.ui.screens.PatternBlueprintScreen
import com.example.ui.screens.ProjectLibraryScreen
import com.example.ui.screens.SewingGuideScreen
import com.example.ui.screens.VirtualFittingScreen
import com.example.ui.theme.AtelierGold
import com.example.ui.theme.AtelierGoldDark
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.MidnightNavy
import com.example.util.AppLanguage
import com.example.util.LocalizationStrings

sealed class NavDestination(val route: String, val titleKey: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Home : NavDestination("home", "nav_home", Icons.Default.Home)
    data object Measure : NavDestination("measure", "nav_measure", Icons.Default.Straighten)
    data object Design : NavDestination("design", "nav_design", Icons.Default.AutoAwesome)
    data object Fit : NavDestination("fit", "nav_fit", Icons.Default.Visibility)
    data object Patterns : NavDestination("patterns", "nav_patterns", Icons.Default.ContentCut)
    data object Sewing : NavDestination("sewing", "nav_sewing", Icons.Default.Handyman)
    data object Cost : NavDestination("cost", "nav_fabric", Icons.Default.Calculate)
    data object Projects : NavDestination("projects", "nav_projects", Icons.Default.Folder)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: TailorViewModel) {
    var currentRoute by remember { mutableStateOf("home") }
    val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isSwahili = language == AppLanguage.SWAHILI

    // Predictive back navigation handling
    if (currentRoute != "home") {
        BackHandler {
            currentRoute = "home"
        }
    }

    val navItems = listOf(
        NavDestination.Home,
        NavDestination.Measure,
        NavDestination.Design,
        NavDestination.Fit,
        NavDestination.Patterns,
        NavDestination.Cost,
        NavDestination.Projects
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(AtelierGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = null,
                                tint = AtelierGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SMART TAILOR AI",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                navigationIcon = {
                    if (currentRoute != "home") {
                        IconButton(
                            onClick = { currentRoute = "home" },
                            modifier = Modifier.testTag("nav_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.setLanguage(if (isSwahili) AppLanguage.ENGLISH else AppLanguage.SWAHILI)
                        },
                        modifier = Modifier.testTag("topbar_lang_button")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AtelierGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isSwahili) "SW" else "EN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = AtelierGoldDark
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("studio_bottom_nav")
            ) {
                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentRoute = item.route },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = LocalizationStrings.t(item.titleKey, language)
                            )
                        },
                        label = {
                            Text(
                                text = LocalizationStrings.t(item.titleKey, language),
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AtelierGoldDark,
                            selectedTextColor = AtelierGoldDark,
                            indicatorColor = AtelierGold.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_${item.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                "home" -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateTo = { currentRoute = it }
                )
                "measure" -> MeasurementScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentRoute = "home" }
                )
                "design" -> DesignStudioScreen(
                    viewModel = viewModel,
                    onNavigateToFit = { currentRoute = "fit" }
                )
                "fit" -> VirtualFittingScreen(
                    viewModel = viewModel,
                    onNavigateToPatterns = { currentRoute = "patterns" }
                )
                "patterns" -> PatternBlueprintScreen(
                    viewModel = viewModel,
                    onNavigateToSewing = { currentRoute = "sewing" }
                )
                "sewing" -> SewingGuideScreen(
                    viewModel = viewModel,
                    onNavigateToCost = { currentRoute = "cost" }
                )
                "cost" -> FabricCostScreen(
                    viewModel = viewModel,
                    onNavigateToProjects = { currentRoute = "projects" }
                )
                "projects" -> ProjectLibraryScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { currentRoute = "home" }
                )
                else -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateTo = { currentRoute = it }
                )
            }
        }
    }
}
