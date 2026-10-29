package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.data.model.GarmentCategory
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

private const val TAG = "TailorAiService"

data class FashionConsultationResult(
    val suggestions: String,
    val recommendedFabrics: List<String>,
    val estimatedMeters: Double,
    val visualPrompt: String
)

class TailorAiService {

    // Lazy initialization of Gemini 3.8 Flash model for consultation, analysis & troubleshooting
    private val textModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.7f
            }
        )
    }

    // Lazy initialization of image generation model
    private val imageModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-nano-banana-2.1",
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            }
        )
    }

    /**
     * Provide expert fashion consultation and generate customized visual prompt
     */
    suspend fun consultFashionDesign(
        userPrompt: String,
        category: GarmentCategory,
        preferredColors: String,
        clientGender: String,
        isSwahili: Boolean
    ): Result<FashionConsultationResult> = withContext(Dispatchers.IO) {
        try {
            val systemInstruction = """
                You are a master haute couture fashion designer, African tailoring specialist, and pattern-maker.
                The client wants to design a garment in the category: ${category.displayName}.
                Client details: $clientGender, Preferred colors: $preferredColors.
                Language preference: ${if (isSwahili) "Swahili (Kiswahili)" else "English"}.
                
                Provide:
                1. Professional fashion consultation analyzing silhouette, drape, neckline, sleeves, and embellishments (e.g., East African Kitenge/Kanga prints, embroidery, French seams).
                2. Recommendations for ideal fabric weights, yardage/meterage estimates.
                3. A detailed visual prompt designed for an image generation model to render a studio fashion sketch/photograph of this bespoke garment.
            """.trimIndent()

            val fullPrompt = "$systemInstruction\n\nClient request:\n$userPrompt"

            val response = withTimeout(60_000L) {
                textModel.generateContent(fullPrompt)
            }

            val text = response.text ?: "Fashion consultation completed."

            val visualPrompt = "High-fashion haute couture photography of a ${category.displayName}, $userPrompt, $preferredColors palette, detailed textile texture, tailored silhouette, clean atelier studio lighting, 8k fashion editorial look."

            val recommendedFabrics = when (category) {
                GarmentCategory.TRADITIONAL_AFRICAN -> listOf("Authentic Wax Print (Kitenge)", "African Brocade", "Kanga Cotton Border")
                GarmentCategory.SHIRT -> listOf("Egyptian Cotton Poplin", "Tanzanian Linen", "Oxford Cotton")
                GarmentCategory.DRESS -> listOf("Silk Mikado", "Cotton Wax Print", "Silk Georgette", "Heavy Crepe")
                GarmentCategory.TROUSERS -> listOf("Wool Worsted", "Stretch Cotton Twill", "Structured Linen")
                GarmentCategory.SUIT -> listOf("Super 120s Wool", "Mohair Blend", "Heavyweight Linen")
                else -> listOf("100% Cotton Weave", "Structured Silk Blend")
            }

            Result.success(
                FashionConsultationResult(
                    suggestions = text,
                    recommendedFabrics = recommendedFabrics,
                    estimatedMeters = if (category == GarmentCategory.DRESS) 3.5 else 2.2,
                    visualPrompt = visualPrompt
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Consultation timed out", e)
            Result.failure(Exception("Fashion consultation timed out. Please check network connection."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Consultation failed", e)
            Result.failure(e)
        }
    }

    /**
     * Analyze fabric photo using multimodal Gemini
     */
    suspend fun analyzeFabricImage(
        bitmap: Bitmap,
        isSwahili: Boolean
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = if (isSwahili) {
                "Changanua picha hii ya kitambaa kama mtaalamu wa nguo. Eleza muundo wa nyuzi (weave texture), mwelekeo wa rangi, makadirio ya unene, na inafaa kushona nguo gani (mfano: Kitenge, Shati, Gauni, au Suti). Toa muhtasari mfupi na wazi."
            } else {
                "Analyze this fabric photograph as a textile specialist. Describe the visible weave texture, drape estimate, color patterns, and best suited garment types (e.g. Kitenge dress, tailored suit, crisp shirt). Keep the analysis practical and concise."
            }

            val response = withTimeout(60_000L) {
                textModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
            }

            val text = response.text ?: "Fabric analyzed successfully."
            Result.success(text)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Fabric analysis timed out", e)
            Result.failure(Exception("Fabric image analysis timed out after 60s."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Fabric analysis failed", e)
            Result.failure(e)
        }
    }

    /**
     * Generate visual garment concept image using Gemini Nano Banana 2.1
     */
    suspend fun generateGarmentImage(prompt: String): Result<Bitmap> = withContext(Dispatchers.IO) {
        try {
            val response = withTimeout(60_000L) {
                imageModel.generateContent(prompt)
            }

            val imagePart = response.candidates.firstOrNull()
                ?.content?.parts?.filterIsInstance<ImagePart>()
                ?.firstOrNull()

            val bitmap = imagePart?.image
            if (bitmap != null) {
                Result.success(bitmap)
            } else {
                Result.failure(Exception("No image part returned in model response."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Garment image generation timed out", e)
            Result.failure(Exception("Image generation timed out after 60 seconds."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Garment image generation failed", e)
            Result.failure(e)
        }
    }

    /**
     * Provide troubleshooting advice for sewing or fitting issues
     */
    suspend fun getSewingTroubleshooting(
        problemDescription: String,
        garmentType: String,
        fabricType: String,
        isSwahili: Boolean
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Expert Master Tailor Troubleshooting:
                Garment Type: $garmentType
                Fabric: $fabricType
                Issue described: $problemDescription
                Language: ${if (isSwahili) "Swahili" else "English"}
                
                Explain:
                1. Root cause (e.g. needle size, thread tension, grainline distortion, pattern ease, pressing technique).
                2. Immediate step-by-step remedy at the sewing machine or cutting table.
                3. Prevention checklist for future garments.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                textModel.generateContent(prompt)
            }
            Result.success(response.text ?: "Remedy formulated.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Troubleshooting query failed", e)
            Result.failure(e)
        }
    }
}
