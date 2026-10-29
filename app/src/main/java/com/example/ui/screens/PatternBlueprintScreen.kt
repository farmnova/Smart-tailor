package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PatternPiece
import com.example.ui.TailorViewModel
import com.example.ui.theme.AtelierGold
import com.example.ui.theme.AtelierGoldDark
import com.example.ui.theme.EmeraldAccent
import com.example.util.AppLanguage
import com.example.util.LocalizationStrings

@Composable
fun PatternBlueprintScreen(
    viewModel: TailorViewModel,
    onNavigateToSewing: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isSwahili = language == AppLanguage.SWAHILI
    val drafting by viewModel.draftingResult.collectAsStateWithLifecycle()
    val design by viewModel.selectedDesign.collectAsStateWithLifecycle()
    val profile by viewModel.selectedProfile.collectAsStateWithLifecycle()

    var fabricWidthInches by remember { mutableDoubleStateOf(44.0) } // 44" Kitenge vs 60" Suiting
    var showExportNotice by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("pattern_blueprint_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = LocalizationStrings.t("pattern_title", language),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = LocalizationStrings.t("pattern_disclaimer", language),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Fabric Bolt Width Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isSwahili) "Upana wa Kitambaa (Bolt Width)" else "Fabric Bolt Width & Cutting Layout",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = fabricWidthInches == 44.0,
                            onClick = {
                                fabricWidthInches = 44.0
                                viewModel.recomputePatternsAndGuide()
                            },
                            label = { Text("44\" / 112 cm (Kitenge / African Wax)", fontSize = 11.sp) },
                            modifier = Modifier.testTag("chip_fabric_44")
                        )
                        FilterChip(
                            selected = fabricWidthInches == 60.0,
                            onClick = {
                                fabricWidthInches = 60.0
                                viewModel.recomputePatternsAndGuide()
                            },
                            label = { Text("60\" / 152 cm (Wool / Silk)", fontSize = 11.sp) },
                            modifier = Modifier.testTag("chip_fabric_60")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isSwahili)
                            "Urefu wa kitambaa unaohitajika: ${drafting?.totalLengthRequiredMeters ?: 2.5} Mita."
                        else
                            "Calculated Fabric Required: ${drafting?.totalLengthRequiredMeters ?: 2.5} Meters.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AtelierGoldDark
                    )
                }
            }
        }

        // Cutting Layout & 5cm Calibration Square Blueprint Canvas
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("blueprint_canvas_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSwahili) "Mchoro wa Mpangilio wa Kukatia" else "Interactive Cutting Layout & Grainlines",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )

                        // 5cm Calibration Badge
                        Box(
                            modifier = Modifier
                                .border(1.dp, AtelierGold, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("5 cm CALIBRATION SQUARE", fontSize = 9.sp, color = AtelierGold, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Architectural Blueprint Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF090D1A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw blueprint grid lines
                            val gridSpacing = 30f
                            for (x in 0..(w / gridSpacing).toInt()) {
                                drawLine(
                                    color = Color(0xFF1E293B),
                                    start = Offset(x * gridSpacing, 0f),
                                    end = Offset(x * gridSpacing, h),
                                    strokeWidth = 1f
                                )
                            }
                            for (y in 0..(h / gridSpacing).toInt()) {
                                drawLine(
                                    color = Color(0xFF1E293B),
                                    start = Offset(0f, y * gridSpacing),
                                    end = Offset(w, y * gridSpacing),
                                    strokeWidth = 1f
                                )
                            }

                            // Fabric Selvedge Edge representation
                            drawLine(
                                color = AtelierGold,
                                start = Offset(15f, 15f),
                                end = Offset(w - 15f, 15f),
                                strokeWidth = 3f
                            )
                            drawLine(
                                color = AtelierGold,
                                start = Offset(15f, h - 15f),
                                end = Offset(w - 15f, h - 15f),
                                strokeWidth = 3f
                            )

                            // Pattern Piece 1: Bodice Front
                            drawRect(
                                color = Color(0xFF0284C7).copy(alpha = 0.35f),
                                topLeft = Offset(30f, 35f),
                                size = Size(w * 0.40f, h * 0.55f)
                            )
                            drawRect(
                                color = Color(0xFF38BDF8),
                                topLeft = Offset(30f, 35f),
                                size = Size(w * 0.40f, h * 0.55f),
                                style = Stroke(width = 2f)
                            )
                            // Seam allowance dashed line
                            drawRect(
                                color = Color.White.copy(alpha = 0.5f),
                                topLeft = Offset(35f, 40f),
                                size = Size(w * 0.40f - 10f, h * 0.55f - 10f),
                                style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f))
                            )

                            // Grainline Arrow on Piece 1
                            drawLine(
                                color = Color.White,
                                start = Offset(30f + (w * 0.20f), 50f),
                                end = Offset(30f + (w * 0.20f), 35f + (h * 0.50f)),
                                strokeWidth = 2f
                            )

                            // Pattern Piece 2: Bodice Back on Fold
                            drawRect(
                                color = Color(0xFF0D9488).copy(alpha = 0.35f),
                                topLeft = Offset(w * 0.48f, 35f),
                                size = Size(w * 0.44f, h * 0.55f)
                            )
                            drawRect(
                                color = Color(0xFF2DD4BF),
                                topLeft = Offset(w * 0.48f, 35f),
                                size = Size(w * 0.44f, h * 0.55f),
                                style = Stroke(width = 2f)
                            )

                            // Pattern Piece 3: Sleeves
                            drawRect(
                                color = Color(0xFF8B5CF6).copy(alpha = 0.35f),
                                topLeft = Offset(30f, h * 0.65f),
                                size = Size(w * 0.35f, h * 0.28f)
                            )
                            drawRect(
                                color = Color(0xFFA78BFA),
                                topLeft = Offset(30f, h * 0.65f),
                                size = Size(w * 0.35f, h * 0.28f),
                                style = Stroke(width = 2f)
                            )

                            // Pattern Piece 4: Collar / Gussets
                            drawRect(
                                color = Color(0xFFD97706).copy(alpha = 0.35f),
                                topLeft = Offset(w * 0.42f, h * 0.65f),
                                size = Size(w * 0.25f, h * 0.28f)
                            )
                            drawRect(
                                color = Color(0xFFFBBF24),
                                topLeft = Offset(w * 0.42f, h * 0.65f),
                                size = Size(w * 0.25f, h * 0.28f),
                                style = Stroke(width = 2f)
                            )

                            // 5cm Exact Calibration Box
                            val squareSize = 45f
                            drawRect(
                                color = AtelierGold.copy(alpha = 0.2f),
                                topLeft = Offset(w - squareSize - 25f, h - squareSize - 25f),
                                size = Size(squareSize, squareSize)
                            )
                            drawRect(
                                color = AtelierGold,
                                topLeft = Offset(w - squareSize - 25f, h - squareSize - 25f),
                                size = Size(squareSize, squareSize),
                                style = Stroke(width = 2f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Dashed line = 1.5 cm Seam Allowance", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Pattern Pieces List
        item {
            Text(
                text = if (isSwahili) "Vipande vya Muundo Vilivyokokotolewa" else "Drafted Pattern Pieces (${drafting?.pieces?.size ?: 0})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        items(drafting?.pieces ?: emptyList()) { piece ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pattern_piece_${piece.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSwahili) piece.swahiliName else piece.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(AtelierGold.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Cut x${piece.cuttingQuantity}${if (piece.cutOnFold) " (FOLD)" else ""}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AtelierGoldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Dimensions: ${piece.widthCm} cm × ${piece.heightCm} cm • Seam Allowance: ${piece.seamAllowanceCm} cm",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (piece.matchingNotches.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Matching Notches: ${piece.matchingNotches.joinToString(", ")}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (piece.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = piece.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Action Buttons: Print / Export & Proceed to Sewing Guide
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { showExportNotice = true },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_export_pattern")
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isSwahili) "Pakua PDF" else "Export Blueprint", fontSize = 12.sp)
                }

                Button(
                    onClick = onNavigateToSewing,
                    colors = ButtonDefaults.buttonColors(containerColor = AtelierGoldDark),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("btn_proceed_to_sewing")
                ) {
                    Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LocalizationStrings.t("sewing_instructions", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (showExportNotice) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldAccent.copy(alpha = 0.15f))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSwahili)
                                "Mchoro wa PDF umehifadhiwa! Print kwa 100% scale bila 'fit to page' na thibitisha kisanduku cha sm 5."
                            else
                                "PDF Blueprint exported! Print at 100% scale without scaling and verify the 5 cm calibration square with a ruler.",
                            fontSize = 12.sp,
                            color = EmeraldAccent,
                            fontWeight = FontWeight.SemiBold
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
