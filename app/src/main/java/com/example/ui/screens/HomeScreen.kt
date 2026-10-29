package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.engine.CostBreakdown
import com.example.ui.TailorViewModel
import com.example.ui.UserRole
import com.example.ui.theme.AtelierGold
import com.example.ui.theme.AtelierGoldDark
import com.example.ui.theme.AtelierSurfaceDark
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.MidnightNavy
import com.example.util.AppLanguage
import com.example.util.LocalizationStrings

@Composable
fun HomeScreen(
    viewModel: TailorViewModel,
    onNavigateTo: (String) -> Unit
) {
    val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val role by viewModel.currentUserRole.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val selectedProfile by viewModel.selectedProfile.collectAsStateWithLifecycle()
    val selectedDesign by viewModel.selectedDesign.collectAsStateWithLifecycle()

    val isSwahili = language == AppLanguage.SWAHILI

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Atelier Brand Header & Language Switcher
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("atelier_hero_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DeepIndigo
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(DeepIndigo, AtelierSurfaceDark, MidnightNavy)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(AtelierGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCut,
                                        contentDescription = "Tailor Shears",
                                        tint = AtelierGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = LocalizationStrings.t("app_title", language),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = LocalizationStrings.t("app_subtitle", language),
                                        fontSize = 12.sp,
                                        color = AtelierGold
                                    )
                                }
                            }

                            // Language Switcher Chip
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = AtelierGold.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .testTag("language_toggle_button")
                                    .clickable {
                                        viewModel.setLanguage(
                                            if (isSwahili) AppLanguage.ENGLISH else AppLanguage.SWAHILI
                                        )
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Language",
                                        tint = AtelierGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isSwahili) "SW" else "EN",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = AtelierGold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = LocalizationStrings.t("welcome_back", language),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = LocalizationStrings.t("tagline", language),
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Role Selector Pills
                        Text(
                            text = if (isSwahili) "Nafasi ya Mtumiaji:" else "Studio Role Mode:",
                            fontSize = 11.sp,
                            color = AtelierGold,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            UserRole.entries.forEach { r ->
                                val isSelected = (r == role)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setUserRole(r) },
                                    label = {
                                        Text(
                                            text = if (isSwahili) r.swahiliLabel else r.label,
                                            fontSize = 11.sp
                                        )
                                    },
                                    modifier = Modifier.testTag("role_${r.name.lowercase()}")
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Working Context Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_context_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isSwahili) "Mteja na Muundo Unaofanyiwa Kazi" else "Current Active Client & Design",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedProfile?.clientName ?: (if (isSwahili) "Hakuna Mteja" else "No Client Selected"),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${selectedProfile?.height ?: 0.0} cm | Bust: ${selectedProfile?.chestBust ?: 0.0} cm",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        AssistChip(
                            onClick = { onNavigateTo("measure") },
                            label = { Text(if (isSwahili) "Badili" else "Edit") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Straighten,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedDesign?.title ?: (if (isSwahili) "Hakuna Muundo" else "No Design Selected"),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = selectedDesign?.category?.displayName ?: "",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        AssistChip(
                            onClick = { onNavigateTo("design") },
                            label = { Text(if (isSwahili) "Buni" else "Design") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        // Quick Action Grid Cards
        item {
            Text(
                text = LocalizationStrings.t("quick_actions", language),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = LocalizationStrings.t("new_measurement", language),
                        subtitle = if (isSwahili) "Pima kwa kadi au rula" else "Card calibration & manual",
                        icon = Icons.Default.Straighten,
                        color = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f),
                        testTag = "action_measure",
                        onClick = { onNavigateTo("measure") }
                    )
                    QuickActionCard(
                        title = LocalizationStrings.t("design_garment", language),
                        subtitle = if (isSwahili) "Ubunifu wa Gemini AI" else "Gemini Fashion Studio",
                        icon = Icons.Default.AutoAwesome,
                        color = AtelierGoldDark,
                        modifier = Modifier.weight(1f),
                        testTag = "action_design",
                        onClick = { onNavigateTo("design") }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = LocalizationStrings.t("nav_fit", language),
                        subtitle = if (isSwahili) "Hakiki muonekano" else "Virtual fitting & approval",
                        icon = Icons.Default.Visibility,
                        color = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f),
                        testTag = "action_fit",
                        onClick = { onNavigateTo("fit") }
                    )
                    QuickActionCard(
                        title = LocalizationStrings.t("nav_patterns", language),
                        subtitle = if (isSwahili) "Mifumo ya kukata" else "Drafted pieces & layout",
                        icon = Icons.Default.ContentCut,
                        color = EmeraldAccent,
                        modifier = Modifier.weight(1f),
                        testTag = "action_patterns",
                        onClick = { onNavigateTo("patterns") }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = LocalizationStrings.t("nav_sewing", language),
                        subtitle = if (isSwahili) "Hatua za kushona" else "Step-by-step assembly",
                        icon = Icons.Default.Handyman,
                        color = Color(0xFFB45309),
                        modifier = Modifier.weight(1f),
                        testTag = "action_sewing",
                        onClick = { onNavigateTo("sewing") }
                    )
                    QuickActionCard(
                        title = LocalizationStrings.t("nav_fabric", language),
                        subtitle = if (isSwahili) "Bei & Mita za Kitambaa" else "Fabric & TZS calculator",
                        icon = Icons.Default.Calculate,
                        color = Color(0xFF059669),
                        modifier = Modifier.weight(1f),
                        testTag = "action_cost",
                        onClick = { onNavigateTo("cost") }
                    )
                }
            }
        }

        // Recent Projects Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalizationStrings.t("recent_projects", language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                OutlinedButton(
                    onClick = { onNavigateTo("projects") },
                    modifier = Modifier.testTag("view_all_projects_button")
                ) {
                    Text(if (isSwahili) "Tazama Zote" else "View All", fontSize = 12.sp)
                }
            }
        }

        if (projects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = if (isSwahili) "Hakuna miradi bado. Anza kwa kubuni nguo mpya!" else "No projects yet. Start by designing a new garment!",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(projects.take(3)) { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateTo("projects") }
                        .testTag("project_item_${project.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = project.projectTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${project.clientName} • ${CostBreakdown.formatTzs(project.sellingPriceTzs)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    if (isSwahili) project.status.swahiliLabel else project.status.label,
                                    fontSize = 11.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
            )
        }
    }
}
