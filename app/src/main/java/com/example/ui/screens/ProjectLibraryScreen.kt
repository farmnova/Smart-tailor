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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.ProjectStatus
import com.example.data.model.SewingProject
import com.example.engine.CostBreakdown
import com.example.ui.TailorViewModel
import com.example.ui.theme.AtelierGold
import com.example.ui.theme.AtelierGoldDark
import com.example.ui.theme.EmeraldAccent
import com.example.util.AppLanguage
import com.example.util.LocalizationStrings

@Composable
fun ProjectLibraryScreen(
    viewModel: TailorViewModel,
    onNavigateToHome: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isSwahili = language == AppLanguage.SWAHILI
    val projects by viewModel.projects.collectAsStateWithLifecycle()

    var selectedStatusFilter by remember { mutableStateOf<ProjectStatus?>(null) }
    var activeDetailProject by remember { mutableStateOf<SewingProject?>(null) }

    val filteredProjects = remember(projects, selectedStatusFilter) {
        if (selectedStatusFilter == null) projects else projects.filter { it.status == selectedStatusFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("project_library_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (isSwahili) "Maktaba ya Miradi ya Karakana" else "Atelier Project Library",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isSwahili)
                    "Fuatilia hatua za ushonaji, idhini za wateja, makadirio ya TZS na nakala za maagizo."
                else
                    "Track production statuses, client approvals, TZS quotations, and project duplicates.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Status Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null },
                    label = { Text(if (isSwahili) "Yote (${projects.size})" else "All (${projects.size})", fontSize = 11.sp) },
                    modifier = Modifier.testTag("filter_all")
                )
                FilterChip(
                    selected = selectedStatusFilter == ProjectStatus.PATTERN_READY,
                    onClick = { selectedStatusFilter = ProjectStatus.PATTERN_READY },
                    label = { Text(if (isSwahili) "Mifumo Tayari" else "Pattern Ready", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedStatusFilter == ProjectStatus.IN_SEWING,
                    onClick = { selectedStatusFilter = ProjectStatus.IN_SEWING },
                    label = { Text(if (isSwahili) "Inashonwa" else "In Sewing", fontSize = 11.sp) }
                )
            }
        }

        if (filteredProjects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = AtelierGoldDark, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSwahili) "Hakuna miradi iliyopatikana kwenye kichujio hiki." else "No projects found under this filter.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredProjects) { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeDetailProject = project }
                        .testTag("project_card_${project.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = project.projectTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.weight(1f)
                            )
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        if (isSwahili) project.status.swahiliLabel else project.status.label,
                                        fontSize = 11.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(14.dp))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${if (isSwahili) "Mteja" else "Client"}: ${project.clientName} • ${CostBreakdown.formatTzs(project.sellingPriceTzs)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Fabric: ${project.fabricMetersRequired} M • Notes: ${project.tailorNotes}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    // Cycle project status forward
                                    val nextStatus = when (project.status) {
                                        ProjectStatus.DRAFT -> ProjectStatus.MEASURED
                                        ProjectStatus.MEASURED -> ProjectStatus.DESIGN_APPROVED
                                        ProjectStatus.DESIGN_APPROVED -> ProjectStatus.PATTERN_READY
                                        ProjectStatus.PATTERN_READY -> ProjectStatus.IN_SEWING
                                        ProjectStatus.IN_SEWING -> ProjectStatus.COMPLETED
                                        ProjectStatus.COMPLETED -> ProjectStatus.DRAFT
                                    }
                                    viewModel.updateProjectStatus(project.id, nextStatus)
                                }
                            ) {
                                Text(if (isSwahili) "Badili Hali" else "Next Status", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Detail & Share Dialog
    activeDetailProject?.let { proj ->
        AlertDialog(
            onDismissRequest = { activeDetailProject = null },
            title = { Text(proj.projectTitle) },
            text = {
                Column {
                    Text("Client: ${proj.clientName}", fontWeight = FontWeight.Bold)
                    Text("Status: ${proj.status.label}")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Production Cost: ${CostBreakdown.formatTzs(proj.totalCostTzs)}")
                    Text("Client Quote: ${CostBreakdown.formatTzs(proj.sellingPriceTzs)}", fontWeight = FontWeight.Bold, color = AtelierGoldDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Fabric Required: ${proj.fabricMetersRequired} Meters")
                    Text("Tailor Instructions: ${proj.tailorNotes}")
                }
            },
            confirmButton = {
                Button(onClick = { activeDetailProject = null }) {
                    Text("Close")
                }
            }
        )
    }
}
