package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.example.data.model.GarmentCategory
import com.example.data.model.GarmentDesign
import com.example.ui.AiTaskState
import com.example.ui.TailorViewModel
import com.example.ui.theme.AtelierGold
import com.example.ui.theme.AtelierGoldDark
import com.example.ui.theme.EmeraldAccent
import com.example.util.AppLanguage
import com.example.util.LocalizationStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesignStudioScreen(
    viewModel: TailorViewModel,
    onNavigateToFit: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isSwahili = language == AppLanguage.SWAHILI
    val activeDesign by viewModel.selectedDesign.collectAsStateWithLifecycle()
    val aiState by viewModel.aiState.collectAsStateWithLifecycle()
    val consultation by viewModel.consultationResult.collectAsStateWithLifecycle()

    var title by remember(activeDesign) { mutableStateOf(activeDesign?.title ?: "Tanzanian Kitenge Gala Attire") }
    var selectedCategory by remember(activeDesign) { mutableStateOf(activeDesign?.category ?: GarmentCategory.TRADITIONAL_AFRICAN) }
    var silhouette by remember(activeDesign) { mutableStateOf(activeDesign?.silhouette ?: "Tailored Royal Silhouette") }
    var collarStyle by remember(activeDesign) { mutableStateOf(activeDesign?.collarStyle ?: "Mandarin Stand Collar") }
    var sleeveStyle by remember(activeDesign) { mutableStateOf(activeDesign?.sleeveStyle ?: "Long Sleeve with Kitenge Border") }
    var neckline by remember(activeDesign) { mutableStateOf(activeDesign?.neckline ?: "Asymmetric Keyhole V-Neck") }
    var embellishments by remember(activeDesign) { mutableStateOf(activeDesign?.embellishments ?: "Gold Zari Thread Embroidery & Beading") }
    var primaryColorHex by remember(activeDesign) { mutableStateOf(activeDesign?.primaryColorHex ?: "#1D2A44") }
    var naturalPrompt by remember(activeDesign) {
        mutableStateOf(activeDesign?.naturalLanguagePrompt ?: "Modern Tanzanian Kitenge gala jacket with intricate gold embroidery along neckline and cuffs")
    }

    val colorSwatches = listOf(
        Pair("#1D2A44", "Midnight Navy"),
        Pair("#D4AF37", "Atelier Gold"),
        Pair("#0F766E", "Emerald Teal"),
        Pair("#831843", "Wine Velvet"),
        Pair("#B45309", "Warm Ochre"),
        Pair("#1E1B4B", "Royal Indigo"),
        Pair("#E2D7C3", "Kilimanjaro Linen")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("design_studio_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = LocalizationStrings.t("design_title", language),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isSwahili)
                    "Buni nguo za kiasili za Kiafrika, suti, magauni na mashati kwa kueleza maono yako au kurekebisha vipengele."
                else
                    "Create contemporary and traditional African garments, bespoke suits, and dresses with conversational AI guidance.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Garment Category Selector
        item {
            Text(
                text = LocalizationStrings.t("style_category", language),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GarmentCategory.entries.forEach { cat ->
                    val isSelected = (cat == selectedCategory)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = cat
                            if (cat == GarmentCategory.TRADITIONAL_AFRICAN) {
                                collarStyle = "Mandarin Stand Collar"
                                embellishments = "Gold Zari Thread Embroidery & Kitenge Borders"
                            }
                        },
                        label = {
                            Text(if (isSwahili) cat.swahiliName else cat.displayName, fontSize = 12.sp)
                        },
                        modifier = Modifier.testTag("chip_category_${cat.name.lowercase()}")
                    )
                }
            }
        }

        // Natural Language Prompt Input with Gemini
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AtelierGoldDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSwahili) "Eleza Muundo kwa Maneno Yako" else "Natural Language Design Prompt",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = naturalPrompt,
                        onValueChange = { naturalPrompt = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_natural_prompt"),
                        minLines = 3,
                        maxLines = 5,
                        placeholder = {
                            Text(
                                if (isSwahili)
                                    "Mfano: Gauni refu la kitenge lenye urembo wa dhahabu kifuani, mikono mirefu na mgongo wa v-neck..."
                                else
                                    "E.g. Elegant floor-length Kitenge gown with gold zari embroidery on chest, flared skirt, and structured cuffs..."
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.consultFashionAi(
                                    prompt = naturalPrompt,
                                    category = selectedCategory,
                                    colors = primaryColorHex
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AtelierGoldDark),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_consult_ai")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSwahili) "Shauriana na Gemini" else "Consult Gemini AI",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.generateGarmentImage(naturalPrompt)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_synthesize_concept")
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSwahili) "Tengeneza Picha" else "Synthesize Concept",
                                fontSize = 12.sp
                            )
                        }
                    }

                    // AI State Indicator
                    when (val state = aiState) {
                        is AiTaskState.Loading -> {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(state.message, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        is AiTaskState.Error -> {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Error: ${state.message}", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                        }
                        is AiTaskState.Success -> {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("✓ ${state.message}", fontSize = 11.sp, color = EmeraldAccent)
                        }
                        is AiTaskState.Idle -> {}
                    }
                }
            }
        }

        // Gemini AI Consultation Result Box
        if (consultation != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2138))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AtelierGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSwahili) "Mapendekezo ya Mbuni wa Gemini" else "Gemini Fashion Master Recommendations",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AtelierGold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = consultation!!.suggestions,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isSwahili) "Vitambaa Vinavyopendekezwa:" else "Recommended Fabrics:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = AtelierGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        consultation!!.recommendedFabrics.forEach { f ->
                            Text("• $f", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }

        // Structural Garment Customizations
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isSwahili) "Ubinafsishaji wa Muundo" else "Garment Structural Customization",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(if (isSwahili) "Jina la Muundo" else "Design Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_design_title")
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = collarStyle,
                        onValueChange = { collarStyle = it },
                        label = { Text(LocalizationStrings.t("collar_type", language)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_collar_style")
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = sleeveStyle,
                        onValueChange = { sleeveStyle = it },
                        label = { Text(LocalizationStrings.t("sleeve_style", language)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_sleeve_style")
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = neckline,
                        onValueChange = { neckline = it },
                        label = { Text(if (isSwahili) "Mstari wa Shingo" else "Neckline Cut") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_neckline")
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = embellishments,
                        onValueChange = { embellishments = it },
                        label = { Text(LocalizationStrings.t("embellishments", language)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_embellishments")
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isSwahili) "Rangi Kuu ya Muundo:" else "Primary Atelier Color Swatch:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        colorSwatches.forEach { (hex, name) ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            val isSelected = (hex == primaryColorHex)
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) AtelierGold else Color.Gray.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .clickable { primaryColorHex = hex }
                                    .testTag("color_swatch_$hex"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = name,
                                        tint = if (hex == "#D4AF37" || hex == "#E2D7C3") Color.Black else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val updatedDesign = GarmentDesign(
                                id = activeDesign?.id ?: java.util.UUID.randomUUID().toString(),
                                title = title,
                                category = selectedCategory,
                                silhouette = silhouette,
                                collarStyle = collarStyle,
                                sleeveStyle = sleeveStyle,
                                neckline = neckline,
                                embellishments = embellishments,
                                primaryColorHex = primaryColorHex,
                                naturalLanguagePrompt = naturalPrompt,
                                isApproved = activeDesign?.isApproved ?: false
                            )
                            viewModel.saveDesign(updatedDesign)
                            onNavigateToFit()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AtelierGoldDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_and_preview_design_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSwahili) "Hifadhi na Jaribu Nguo" else "Save & Open Virtual Fitting",
                            fontWeight = FontWeight.Bold
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
