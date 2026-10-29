package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ai.TailorAiService
import com.example.ui.TailorViewModel
import com.example.ui.theme.AtelierGold
import com.example.ui.theme.AtelierGoldDark
import com.example.ui.theme.EmeraldAccent
import com.example.util.AppLanguage
import com.example.util.LocalizationStrings
import kotlinx.coroutines.launch

@Composable
fun SewingGuideScreen(
    viewModel: TailorViewModel,
    onNavigateToCost: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isSwahili = language == AppLanguage.SWAHILI
    val guide by viewModel.sewingGuide.collectAsStateWithLifecycle()
    val design by viewModel.selectedDesign.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Steps, 1: Tools & Prep, 2: QC & AI Troubleshooter

    // Track completed steps
    val completedSteps = remember { mutableStateListOf<Int>() }

    // Tailor Troubleshooter state
    val scope = rememberCoroutineScope()
    var troubleIssue by remember { mutableStateOf("") }
    var aiSolution by remember { mutableStateOf<String?>(null) }
    var isLoadingSolution by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("sewing_guide_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = LocalizationStrings.t("sewing_instructions", language),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${design?.title ?: "Garment"} • ${if (isSwahili) guide?.swahiliSkillLevel ?: "Kati" else guide?.skillLevel ?: "Intermediate"}",
                fontSize = 12.sp,
                color = AtelierGoldDark,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Navigation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(if (isSwahili) "Hatua za Kushona" else "Assembly Steps", fontSize = 12.sp) },
                    modifier = Modifier.testTag("tab_steps")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(if (isSwahili) "Vifaa & Maandalizi" else "Tools & Prep", fontSize = 12.sp) },
                    modifier = Modifier.testTag("tab_tools")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(if (isSwahili) "Uhakiki & Msaada wa AI" else "QC & AI Help", fontSize = 12.sp) },
                    modifier = Modifier.testTag("tab_qc")
                )
            }
        }

        if (selectedTab == 0) {
            // Step-by-Step Interactive Assembly Cards
            itemsIndexed(guide?.steps ?: emptyList()) { index, step ->
                val isDone = completedSteps.contains(step.stepNumber)
                var expanded by remember { mutableStateOf(true) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sewing_step_${step.stepNumber}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDone) EmeraldAccent.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isDone) EmeraldAccent else AtelierGoldDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${step.stepNumber}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isSwahili) step.swahiliTitle else step.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isDone,
                                    onCheckedChange = { checked ->
                                        if (checked) completedSteps.add(step.stepNumber) else completedSteps.remove(step.stepNumber)
                                    },
                                    modifier = Modifier.testTag("check_step_${step.stepNumber}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSwahili) step.swahiliInstruction else step.instruction,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Stitch & Allowance Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Stitch: ${step.stitchType}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Seam: ${step.seamAllowance}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Fitting Checkpoint Callout
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AtelierGold.copy(alpha = 0.12f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = AtelierGoldDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Checkpoint: ${step.checkpoint}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        } else if (selectedTab == 1) {
            // Tools & Preparation View
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isSwahili) "Maandalizi ya Kitambaa (Pre-washing & Grain)" else "Fabric Preparation & Grain Alignment",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSwahili) guide?.swahiliFabricPreparation ?: "" else guide?.fabricPreparation ?: "",
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isSwahili) "Vifaa Vinavyohitajika Karakana" else "Required Atelier Tools",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val tools = if (isSwahili) guide?.swahiliToolsRequired else guide?.toolsRequired
                        tools?.forEach { tool ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = tool, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // Quality Control & Gemini AI Troubleshooter
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isSwahili) "Orodha ya Uhakiki wa Ubora (Quality Control)" else "Couture Quality-Control Checklist",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val qcs = if (isSwahili) guide?.swahiliQualityChecklist else guide?.qualityChecklist
                        qcs?.forEach { qc ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = qc, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Gemini Master Tailor Troubleshooter
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2034))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AtelierGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSwahili) "Msaada wa Cherehani kutoka kwa Gemini AI" else "Gemini Master Tailor Troubleshooter",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AtelierGold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSwahili)
                                "Una tatizo la cherehani kuvuta uzi, pindo kukunyata, au kola kutoonekana sawa? Uliza hapa upate suluhisho mara moja:"
                            else
                                "Encountering fabric puckering, bird-nesting threads, wavy hems, or collar roll issues? Describe the fault:",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = troubleIssue,
                            onValueChange = { troubleIssue = it },
                            placeholder = { Text(if (isSwahili) "Mfano: Kitambaa cha kitenge kinajikunja kwenye pindo la shingo..." else "E.g. Kitenge fabric is puckering along the neckline curve...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_troubleshoot")
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (troubleIssue.isNotBlank()) {
                                    scope.launch {
                                        isLoadingSolution = true
                                        val ai = TailorAiService()
                                        val res = ai.getSewingTroubleshooting(
                                            problemDescription = troubleIssue,
                                            garmentType = design?.category?.displayName ?: "Garment",
                                            fabricType = "Kitenge / Cotton",
                                            isSwahili = isSwahili
                                        )
                                        aiSolution = res.getOrNull() ?: "Unable to fetch solution."
                                        isLoadingSolution = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AtelierGoldDark),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_solve_issue")
                        ) {
                            if (isLoadingSolution) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(if (isSwahili) "Pata Suluhisho la AI" else "Diagnose & Get Solution", fontWeight = FontWeight.Bold)
                        }

                        if (aiSolution != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (isSwahili) "Ufumbuzi:" else "Tailor Prescription:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AtelierGold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = aiSolution!!,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f),
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onNavigateToCost,
                colors = ButtonDefaults.buttonColors(containerColor = AtelierGoldDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_proceed_to_cost")
            ) {
                Text(
                    text = if (isSwahili) "Fungua Kokotoo la Gharama (TZS)" else "Open Fabric & TZS Cost Calculator",
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
