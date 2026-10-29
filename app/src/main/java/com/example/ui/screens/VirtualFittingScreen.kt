package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GarmentCategory
import com.example.engine.CostBreakdown
import com.example.ui.TailorViewModel
import com.example.ui.theme.AtelierGold
import com.example.ui.theme.AtelierGoldDark
import com.example.ui.theme.EmeraldAccent
import com.example.util.AppLanguage
import com.example.util.LocalizationStrings

@Composable
fun VirtualFittingScreen(
    viewModel: TailorViewModel,
    onNavigateToPatterns: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isSwahili = language == AppLanguage.SWAHILI
    val design by viewModel.selectedDesign.collectAsStateWithLifecycle()
    val profile by viewModel.selectedProfile.collectAsStateWithLifecycle()
    val cost by viewModel.costBreakdown.collectAsStateWithLifecycle()
    val generatedBitmap by viewModel.generatedConceptImage.collectAsStateWithLifecycle()

    var isFrontView by remember { mutableStateOf(true) }
    var showWireframeOverlay by remember { mutableStateOf(true) }
    var showRevisionDialog by remember { mutableStateOf(false) }
    var revisionNotes by remember { mutableStateOf("") }

    val garmentColor = remember(design?.primaryColorHex) {
        try {
            Color(android.graphics.Color.parseColor(design?.primaryColorHex ?: "#1D2A44"))
        } catch (_: Exception) {
            Color(0xFF1D2A44)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("virtual_fitting_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = LocalizationStrings.t("fitting_title", language),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Mandatory Visual Approximation Label
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Notice",
                        tint = AtelierGoldDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = LocalizationStrings.t("visual_approximation_notice", language),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Virtual Fitting Display Area
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fitting_canvas_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121424))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Controls: Front/Side toggle & Wireframe toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = isFrontView,
                                onClick = { isFrontView = true },
                                label = { Text(LocalizationStrings.t("front_view", language), fontSize = 11.sp) },
                                modifier = Modifier.testTag("chip_front_view")
                            )
                            FilterChip(
                                selected = !isFrontView,
                                onClick = { isFrontView = false },
                                label = { Text(LocalizationStrings.t("side_view", language), fontSize = 11.sp) },
                                modifier = Modifier.testTag("chip_side_view")
                            )
                        }

                        FilterChip(
                            selected = showWireframeOverlay,
                            onClick = { showWireframeOverlay = !showWireframeOverlay },
                            label = { Text(if (isSwahili) "Mishono" else "Pattern Seams", fontSize = 11.sp) },
                            modifier = Modifier.testTag("chip_seam_overlay")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fitting Visual Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF1B1E38), Color(0xFF0F1122))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (generatedBitmap != null) {
                            Image(
                                bitmap = generatedBitmap!!.asImageBitmap(),
                                contentDescription = "AI Generated Garment Preview",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }

                        // Mathematical 2D drape & seam simulation overlay
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val cx = w / 2f

                            val isDress = design?.category == GarmentCategory.DRESS
                            val isTrouser = design?.category == GarmentCategory.TROUSERS

                            // Mannequin base silhouette
                            drawCircle(
                                color = Color.White.copy(alpha = 0.2f),
                                radius = 22f,
                                center = Offset(cx, 45f)
                            )

                            val garmentPath = Path().apply {
                                if (isDress) {
                                    // Dress silhouette
                                    moveTo(cx - 50f, 85f)
                                    lineTo(cx + 50f, 85f) // Shoulders
                                    lineTo(cx + 42f, 150f) // Bust/Waist
                                    lineTo(cx + 90f, h - 35f) // Flared Hem
                                    lineTo(cx - 90f, h - 35f)
                                    lineTo(cx - 42f, 150f)
                                    close()
                                } else if (isTrouser) {
                                    // Trouser silhouette
                                    moveTo(cx - 45f, 120f)
                                    lineTo(cx + 45f, 120f) // Waistband
                                    lineTo(cx + 42f, 160f) // Hips
                                    lineTo(cx + 36f, h - 25f) // Right leg hem
                                    lineTo(cx + 8f, h - 25f)
                                    lineTo(cx, 185f) // Crotch fork
                                    lineTo(cx - 8f, h - 25f)
                                    lineTo(cx - 36f, h - 25f) // Left leg hem
                                    lineTo(cx - 42f, 160f)
                                    close()
                                } else {
                                    // Traditional tunic / Shirt
                                    moveTo(cx - 55f, 85f)
                                    lineTo(cx + 55f, 85f)
                                    lineTo(cx + 58f, h - 60f)
                                    lineTo(cx - 58f, h - 60f)
                                    close()
                                }
                            }

                            // If no generated image, render styled garment drape shape
                            if (generatedBitmap == null) {
                                drawPath(
                                    path = garmentPath,
                                    color = garmentColor.copy(alpha = 0.88f)
                                )
                            }

                            // Wireframe Seam Overlay Lines
                            if (showWireframeOverlay) {
                                // Seam lines in Atelier Gold
                                drawPath(
                                    path = garmentPath,
                                    color = AtelierGold,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                                        width = 2.5f,
                                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                    )
                                )

                                // Princess / dart seam lines
                                drawLine(
                                    color = AtelierGold.copy(alpha = 0.7f),
                                    start = Offset(cx - 24f, 85f),
                                    end = Offset(cx - 20f, 180f),
                                    strokeWidth = 2f
                                )
                                drawLine(
                                    color = AtelierGold.copy(alpha = 0.7f),
                                    start = Offset(cx + 24f, 85f),
                                    end = Offset(cx + 20f, 180f),
                                    strokeWidth = 2f
                                )

                                // Waistline anchor
                                drawLine(
                                    color = Color.White.copy(alpha = 0.6f),
                                    start = Offset(cx - 45f, 150f),
                                    end = Offset(cx + 45f, 150f),
                                    strokeWidth = 1.5f
                                )
                            }
                        }

                        // Calibration indicator tag
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Bust: ${profile?.chestBust ?: 0.0} cm | Waist: ${profile?.waist ?: 0.0} cm",
                                fontSize = 10.sp,
                                color = AtelierGold
                            )
                        }

                        // Front/Side View Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isFrontView) "FRONT ATELIER VIEW" else "PROFILE SIDE VIEW",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = design?.title ?: "Garment Specification",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${design?.silhouette} • ${design?.collarStyle} • ${design?.sleeveStyle}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Design Approval & Pattern Generation Gate
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("approval_gate_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (design?.isApproved == true) EmeraldAccent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSwahili) "Idhini ya Muundo na Gharama" else "Design Approval & Costing Gate",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        if (design?.isApproved == true) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isSwahili) "Umeidhinishwa" else "Approved",
                                    fontSize = 11.sp,
                                    color = EmeraldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isSwahili)
                            "Kabla ya kutoa mifumo rasmi ya kukatia nguo (patterns), thibitisha muundo na vipimo. Unaweza pia kuomba marekebisho."
                        else
                            "Production patterns are only generated once the client or tailor approves the design and confirms measurements.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Summary details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(if (isSwahili) "Mita za Kitambaa:" else "Fabric Required:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${cost?.requiredMeters ?: 2.5} M", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column {
                            Text(if (isSwahili) "Makadirio ya Bei:" else "Estimated Quote:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(cost?.formattedSellingPrice() ?: "160,000 TZS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AtelierGoldDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showRevisionDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_request_revision")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(LocalizationStrings.t("request_revision", language), fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.updateDesignApproval(true)
                                onNavigateToPatterns()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("btn_approve_design")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = LocalizationStrings.t("approve_design", language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showRevisionDialog) {
        AlertDialog(
            onDismissRequest = { showRevisionDialog = false },
            title = { Text(LocalizationStrings.t("request_revision", language)) },
            text = {
                Column {
                    Text(
                        text = if (isSwahili)
                            "Weka maelezo ya mabadiliko unayotaka (mfano: fupisha mikono, ongeza mpasuo, badili kola kuwa ya Kichina):"
                        else
                            "Specify the requested modifications (e.g., shorten sleeves, deepen neckline, change to mandarin collar):",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = revisionNotes,
                        onValueChange = { revisionNotes = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Revision instructions...") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateDesignApproval(false, revisionNotes)
                        showRevisionDialog = false
                    }
                ) {
                    Text(if (isSwahili) "Wasilisha" else "Submit Revision")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRevisionDialog = false }) {
                    Text(if (isSwahili) "Ghairi" else "Cancel")
                }
            }
        )
    }
}
