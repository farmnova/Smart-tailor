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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.FabricItem
import com.example.engine.CostBreakdown
import com.example.ui.TailorViewModel
import com.example.ui.theme.AtelierGold
import com.example.ui.theme.AtelierGoldDark
import com.example.ui.theme.EmeraldAccent
import com.example.util.AppLanguage
import com.example.util.LocalizationStrings

@Composable
fun FabricCostScreen(
    viewModel: TailorViewModel,
    onNavigateToProjects: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isSwahili = language == AppLanguage.SWAHILI
    val fabrics by viewModel.fabrics.collectAsStateWithLifecycle()
    val selectedFabric by viewModel.selectedFabric.collectAsStateWithLifecycle()
    val cost by viewModel.costBreakdown.collectAsStateWithLifecycle()
    val design by viewModel.selectedDesign.collectAsStateWithLifecycle()
    val profile by viewModel.selectedProfile.collectAsStateWithLifecycle()

    var tailorLaborTzs by remember { mutableDoubleStateOf(45000.0) }
    var markupPercent by remember { mutableIntStateOf(35) }
    var projectTitle by remember { mutableStateOf("Kitenge Gala Order - " + (profile?.clientName ?: "Client")) }
    var projectSavedNotice by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("fabric_cost_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = LocalizationStrings.t("fabric_calculator_title", language),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Disclaimer on fabric testing
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
                        contentDescription = "Info",
                        tint = AtelierGoldDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isSwahili)
                            "Kumbuka: Picha ya kitambaa inatoa makadirio ya muonekano. Asilimia halisi ya pamba au hariri inahitaji kupimwa kitaalamu kabla ya mkataba."
                        else
                            "Notice: Visual fabric analysis provides estimates. Physical fiber testing and swatch inspection are advised before final commercial quotation.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Fabric Swatches Selector
        item {
            Text(
                text = if (isSwahili) "Chagua Kitambaa kutoka Maktaba ya Karakana" else "Select Atelier Fabric Swatch",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                fabrics.forEach { f ->
                    val isSelected = (f.id == selectedFabric?.id)
                    val swatchColor = try {
                        Color(android.graphics.Color.parseColor(f.primaryColorHex))
                    } catch (_: Exception) {
                        Color.Gray
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectFabric(f) }
                            .testTag("fabric_item_${f.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) AtelierGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(swatchColor),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = f.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = "${f.fabricType} • ${f.widthInches.toInt()}\" Width • ${CostBreakdown.formatTzs(f.pricePerMeterTzs)} / m",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Selected Fabric Detailed Spec Card
        item {
            selectedFabric?.let { f ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isSwahili) "Sifa za Kitambaa Kilichochaguliwa" else "Selected Fabric Specifications",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = f.geminiDescription, fontSize = 12.sp, lineHeight = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Weight: ${f.weightGsm.toInt()} GSM", fontSize = 11.sp)
                            Text("Stretch: ${f.stretchPercentage.toInt()}%", fontSize = 11.sp)
                            Text("Stock: ${f.quantityAvailableMeters} M", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // TZS Financial Cost Calculator Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cost_calculator_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isSwahili) "Mchanganuo wa Gharama za Ushonaji (TZS)" else "Bespoke Production Costing (TZS)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Fabric cost row
                    CostLineItem(
                        label = if (isSwahili) "Kitambaa (${cost?.requiredMeters ?: 0.0}m @ ${CostBreakdown.formatTzs(selectedFabric?.pricePerMeterTzs ?: 15000.0)})" else "Fabric (${cost?.requiredMeters ?: 0.0}m)",
                        amount = cost?.formattedFabric() ?: "0 TZS"
                    )

                    // Notions & trimmings row
                    CostLineItem(
                        label = if (isSwahili) "Vifaa (Zipu, Vifungo, Interfacing, Uzi)" else "Notions (Zippers, Buttons, Thread)",
                        amount = cost?.formattedNotions() ?: "0 TZS"
                    )

                    // Embroidery & handcraft
                    CostLineItem(
                        label = if (isSwahili) "Nakshi za Dhahabu / Urembo wa Mkono" else "Embroidery & Couture Embellishments",
                        amount = cost?.formattedEmbroidery() ?: "0 TZS"
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    // Tailor Labor Slider / Input
                    Text(
                        text = "${if (isSwahili) "Gharama ya Kazi ya Fundi:" else "Tailor Labor Fee:"} ${CostBreakdown.formatTzs(tailorLaborTzs)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = tailorLaborTzs.toFloat(),
                        onValueChange = {
                            tailorLaborTzs = it.toDouble()
                            viewModel.recomputeCost(laborTzs = tailorLaborTzs, markupPercent = markupPercent)
                        },
                        valueRange = 15000f..150000f,
                        steps = 26,
                        modifier = Modifier.testTag("slider_labor")
                    )

                    // Profit Margin Slider
                    Text(
                        text = "${if (isSwahili) "Faida ya Biashara:" else "Workshop Profit Margin:"} $markupPercent%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = markupPercent.toFloat(),
                        onValueChange = {
                            markupPercent = it.toInt()
                            viewModel.recomputeCost(laborTzs = tailorLaborTzs, markupPercent = markupPercent)
                        },
                        valueRange = 10f..75f,
                        steps = 12,
                        modifier = Modifier.testTag("slider_markup")
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    // Production Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isSwahili) "Jumla ya Gharama za Utengenezaji:" else "Total Production Cost:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = cost?.formattedTotal() ?: "0 TZS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Final Customer Quote
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AtelierGold.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSwahili) "BEI YA KUMTOZA MTEJA:" else "SUGGESTED CLIENT QUOTE:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AtelierGoldDark
                        )
                        Text(
                            text = cost?.formattedSellingPrice() ?: "0 TZS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AtelierGoldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save as Official Project Order
                    OutlinedTextField(
                        value = projectTitle,
                        onValueChange = { projectTitle = it },
                        label = { Text(if (isSwahili) "Jina la Mradi / Agizo" else "Project / Order Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.createProjectFromCurrent(
                                title = projectTitle,
                                clientName = profile?.clientName ?: "Client",
                                notes = "Order confirmed. Measurements and fabric specifications verified."
                            )
                            projectSavedNotice = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AtelierGoldDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_save_project_order")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSwahili) "Hifadhi Agizo kwenye Miradi" else "Save Order to Atelier Projects",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (projectSavedNotice) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSwahili) "Mradi umehifadhiwa kikamilifu!" else "Project saved successfully to library!",
                            color = EmeraldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = onNavigateToProjects,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_view_projects_library")
            ) {
                Text(if (isSwahili) "Tazama Maktaba ya Miradi" else "View Projects Library")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CostLineItem(label: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = amount, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
