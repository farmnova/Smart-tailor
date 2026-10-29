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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CalibrationMethod
import com.example.data.model.MeasurementProfile
import com.example.data.model.MeasurementUnit
import com.example.engine.MeasurementCalibrationEngine
import com.example.ui.TailorViewModel
import com.example.ui.theme.AtelierGold
import com.example.ui.theme.AtelierGoldDark
import com.example.ui.theme.EmeraldAccent
import com.example.util.AppLanguage
import com.example.util.LocalizationStrings

@Composable
fun MeasurementScreen(
    viewModel: TailorViewModel,
    onNavigateBack: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isSwahili = language == AppLanguage.SWAHILI
    val activeProfile by viewModel.selectedProfile.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Reference Scale Calibration, 1: Manual Form

    // Form state initialized from active profile
    var clientName by remember(activeProfile) { mutableStateOf(activeProfile?.clientName ?: "Amina Juma") }
    var gender by remember(activeProfile) { mutableStateOf(activeProfile?.gender ?: "Female") }
    var unit by remember(activeProfile) { mutableStateOf(activeProfile?.unit ?: MeasurementUnit.CENTIMETERS) }

    var height by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.height ?: 168.0) }
    var shoulder by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.shoulderWidth ?: 40.0) }
    var chest by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.chestBust ?: 92.0) }
    var waist by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.waist ?: 74.0) }
    var hips by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.hips ?: 98.0) }
    var neck by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.neck ?: 36.0) }
    var sleeve by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.sleeveLength ?: 58.0) }
    var backLength by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.backLength ?: 41.0) }
    var garmentLength by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.desiredGarmentLength ?: 100.0) }
    var trouserWaist by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.trouserWaist ?: 76.0) }
    var inseam by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.inseam ?: 74.0) }
    var outseam by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.outseam ?: 100.0) }
    var thigh by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.thighCircumference ?: 54.0) }
    var legOpening by remember(activeProfile) { mutableDoubleStateOf(activeProfile?.legOpening ?: 36.0) }

    // Calibration simulation states
    var cardDetected by remember { mutableStateOf(true) }
    var isConfirmed by remember { mutableStateOf(true) }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("measurement_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = LocalizationStrings.t("measurements_title", language),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Privacy & Non-hallucination Consent Banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Privacy",
                        tint = AtelierGoldDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = LocalizationStrings.t("measurement_disclaimer", language),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Calibration Mode Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(if (isSwahili) "Pima kwa Kadi / Rula" else "Reference Calibration", fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_calibration")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(if (isSwahili) "Fomu ya Mkono" else "Manual Measurements", fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Straighten, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_manual")
                )
            }
        }

        if (selectedTab == 0) {
            // Reference Calibration View
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                                text = if (isSwahili) "Mwongozo wa Kamera na Ulinganifu" else "Camera Alignment & Calibration",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (cardDetected) EmeraldAccent else Color.Red)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (cardDetected) "Scale Verified (94%)" else "Uncalibrated",
                                    fontSize = 11.sp,
                                    color = if (cardDetected) EmeraldAccent else Color.Red,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isSwahili)
                                "1. Shikilia kadi ya benki au rula kifuani.\n2. Simama wima mita 2.5 kutoka kwenye simu.\n3. Hakikisha mwili wote unaonekana kuanzia kichwani hadi miguuni."
                            else
                                "1. Hold a standard ID card (8.56 cm) flat at chest level.\n2. Stand upright 2.5m from the camera.\n3. Keep arms slightly apart and ensure head-to-toe visibility.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Silhouette Canvas with Measurement Markers
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF161828)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val centerX = size.width / 2f
                                val topY = 25f
                                val bottomY = size.height - 25f

                                // Grid markings
                                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

                                // Head
                                drawCircle(
                                    color = AtelierGold,
                                    radius = 16f,
                                    center = Offset(centerX, topY + 20f)
                                )
                                // Spine line
                                drawLine(
                                    color = Color.White.copy(alpha = 0.4f),
                                    start = Offset(centerX, topY + 36f),
                                    end = Offset(centerX, bottomY - 60f),
                                    strokeWidth = 2f,
                                    pathEffect = pathEffect
                                )
                                // Shoulder marker
                                drawLine(
                                    color = AtelierGold,
                                    start = Offset(centerX - 50f, topY + 48f),
                                    end = Offset(centerX + 50f, topY + 48f),
                                    strokeWidth = 3f
                                )
                                // Chest marker
                                drawLine(
                                    color = Color(0xFF38BDF8),
                                    start = Offset(centerX - 42f, topY + 75f),
                                    end = Offset(centerX + 42f, topY + 75f),
                                    strokeWidth = 3f
                                )
                                // Waist marker
                                drawLine(
                                    color = AtelierGold,
                                    start = Offset(centerX - 35f, topY + 105f),
                                    end = Offset(centerX + 35f, topY + 105f),
                                    strokeWidth = 3f
                                )
                                // Hips marker
                                drawLine(
                                    color = Color(0xFF38BDF8),
                                    start = Offset(centerX - 45f, topY + 130f),
                                    end = Offset(centerX + 45f, topY + 130f),
                                    strokeWidth = 3f
                                )
                                // Inseam to feet
                                drawLine(
                                    color = Color.White.copy(alpha = 0.5f),
                                    start = Offset(centerX - 25f, bottomY),
                                    end = Offset(centerX, topY + 140f),
                                    strokeWidth = 2f
                                )
                                drawLine(
                                    color = Color.White.copy(alpha = 0.5f),
                                    start = Offset(centerX + 25f, bottomY),
                                    end = Offset(centerX, topY + 140f),
                                    strokeWidth = 2f
                                )

                                // Reference Card Marker
                                drawRect(
                                    color = Color(0xFFF59E0B),
                                    topLeft = Offset(centerX - 18f, topY + 68f),
                                    size = androidx.compose.ui.geometry.Size(36f, 22f)
                                )
                            }

                            // Overlay Tag
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Ref: 8.56 cm | 1px ≈ 0.28mm",
                                    fontSize = 10.sp,
                                    color = AtelierGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    // Trigger scale re-calibration
                                    cardDetected = !cardDetected
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("toggle_calibration_scale_button")
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isSwahili) "Pima Upya" else "Re-Calibrate", fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    viewModel.calibrateFromScale(
                                        clientName = clientName,
                                        gender = gender,
                                        cardPixelWidth = 125f,
                                        bodyHeightPixels = 600f,
                                        shoulderPixels = 145f,
                                        chestPixels = 120f,
                                        waistPixels = 98f,
                                        hipPixels = 128f
                                    )
                                    isConfirmed = true
                                    showSavedSnackbar = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AtelierGoldDark),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("confirm_calibrated_measurements_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isSwahili) "Thibitisha" else "Apply Scale", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Editable Measurements Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isSwahili) "Maelezo ya Mteja na Vipimo Vinavyohaririwa" else "Client Details & Editable Measurements",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text(if (isSwahili) "Jina la Mteja" else "Client Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_client_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Female", "Male", "Unisex").forEach { g ->
                            FilterChip(
                                selected = gender == g,
                                onClick = { gender = g },
                                label = { Text(g, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isSwahili) "Vipimo vya Mwili wa Juu (cm)" else "Upper Body Dimensions (cm)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    MeasurementInputRow(label = LocalizationStrings.t("height", language), value = height, onValueChange = { height = it }, testTag = "input_height")
                    MeasurementInputRow(label = LocalizationStrings.t("shoulder_width", language), value = shoulder, onValueChange = { shoulder = it }, testTag = "input_shoulder")
                    MeasurementInputRow(label = LocalizationStrings.t("chest_bust", language), value = chest, onValueChange = { chest = it }, testTag = "input_chest")
                    MeasurementInputRow(label = LocalizationStrings.t("waist", language), value = waist, onValueChange = { waist = it }, testTag = "input_waist")
                    MeasurementInputRow(label = LocalizationStrings.t("hips", language), value = hips, onValueChange = { hips = it }, testTag = "input_hips")
                    MeasurementInputRow(label = LocalizationStrings.t("neck", language), value = neck, onValueChange = { neck = it }, testTag = "input_neck")
                    MeasurementInputRow(label = LocalizationStrings.t("sleeve_length", language), value = sleeve, onValueChange = { sleeve = it }, testTag = "input_sleeve")
                    MeasurementInputRow(label = LocalizationStrings.t("back_length", language), value = backLength, onValueChange = { backLength = it }, testTag = "input_back_length")
                    MeasurementInputRow(label = LocalizationStrings.t("garment_length", language), value = garmentLength, onValueChange = { garmentLength = it }, testTag = "input_garment_length")

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isSwahili) "Vipimo vya Suruali na Mwili wa Chini (cm)" else "Trouser & Lower Body Dimensions (cm)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    MeasurementInputRow(label = LocalizationStrings.t("trouser_waist", language), value = trouserWaist, onValueChange = { trouserWaist = it }, testTag = "input_trouser_waist")
                    MeasurementInputRow(label = LocalizationStrings.t("inseam", language), value = inseam, onValueChange = { inseam = it }, testTag = "input_inseam")
                    MeasurementInputRow(label = LocalizationStrings.t("outseam", language), value = outseam, onValueChange = { outseam = it }, testTag = "input_outseam")
                    MeasurementInputRow(label = LocalizationStrings.t("thigh", language), value = thigh, onValueChange = { thigh = it }, testTag = "input_thigh")
                    MeasurementInputRow(label = LocalizationStrings.t("leg_opening", language), value = legOpening, onValueChange = { legOpening = it }, testTag = "input_leg_opening")

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val profile = MeasurementProfile(
                                id = activeProfile?.id ?: java.util.UUID.randomUUID().toString(),
                                clientName = clientName,
                                gender = gender,
                                unit = unit,
                                height = height,
                                shoulderWidth = shoulder,
                                chestBust = chest,
                                waist = waist,
                                hips = hips,
                                neck = neck,
                                sleeveLength = sleeve,
                                armCircumference = (chest * 0.33),
                                backLength = backLength,
                                desiredGarmentLength = garmentLength,
                                trouserWaist = trouserWaist,
                                inseam = inseam,
                                outseam = outseam,
                                thighCircumference = thigh,
                                legOpening = legOpening,
                                calibrationMethod = if (selectedTab == 0) CalibrationMethod.STANDARD_CARD else CalibrationMethod.MANUAL_ENTRY,
                                confidenceScore = 95,
                                isConfirmedByUser = true
                            )
                            viewModel.saveMeasurementProfile(profile)
                            showSavedSnackbar = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_measurements_profile_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AtelierGoldDark)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(LocalizationStrings.t("save_profile", language), fontWeight = FontWeight.Bold)
                    }

                    if (showSavedSnackbar) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSwahili) "Vipimo vimehifadhiwa na kuthibitishwa kwa mafanikio!" else "Measurements saved & confirmed successfully!",
                            color = EmeraldAccent,
                            fontSize = 12.sp,
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

@Composable
fun MeasurementInputRow(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.5f)
        )
        OutlinedTextField(
            value = if (value % 1.0 == 0.0) value.toInt().toString() else value.toString(),
            onValueChange = { str ->
                val num = str.toDoubleOrNull()
                if (num != null) onValueChange(num)
            },
            singleLine = true,
            modifier = Modifier
                .width(110.dp)
                .testTag(testTag)
        )
    }
}
