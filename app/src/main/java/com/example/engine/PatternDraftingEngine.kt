package com.example.engine

import com.example.data.model.GarmentCategory
import com.example.data.model.GarmentDesign
import com.example.data.model.MeasurementProfile
import com.example.data.model.PatternPiece
import kotlin.math.roundToInt

data class DraftingResult(
    val pieces: List<PatternPiece>,
    val fabricWidthInches: Double,
    val totalLengthRequiredMeters: Double,
    val cuttingNotes: String,
    val swahiliCuttingNotes: String,
    val calibrationSquareSizeCm: Double = 5.0
)

object PatternDraftingEngine {
    private const val SEAM_ALLOWANCE_CM = 1.5

    fun draftPattern(
        design: GarmentDesign,
        profile: MeasurementProfile,
        fabricWidthInches: Double = 44.0
    ): DraftingResult {
        val pieces = when (design.category) {
            GarmentCategory.SHIRT -> draftShirt(profile)
            GarmentCategory.DRESS -> draftDress(profile)
            GarmentCategory.TROUSERS -> draftTrousers(profile)
            GarmentCategory.TRADITIONAL_AFRICAN -> draftTraditional(profile)
            GarmentCategory.SKIRT -> draftSkirt(profile)
            GarmentCategory.SUIT -> draftSuit(profile)
            GarmentCategory.UNIFORM -> draftShirt(profile)
        }

        // Calculate layout length based on fabric width
        val fabricWidthCm = fabricWidthInches * 2.54
        var totalLengthCm = 0.0
        var currentLineWidth = 0.0
        var maxRowHeight = 0.0

        for (piece in pieces) {
            val effectiveWidth = piece.widthCm + (piece.seamAllowanceCm * 2)
            val effectiveHeight = piece.heightCm + (piece.seamAllowanceCm * 2)

            for (i in 0 until piece.cuttingQuantity) {
                if (currentLineWidth + effectiveWidth <= fabricWidthCm) {
                    currentLineWidth += effectiveWidth
                    if (effectiveHeight > maxRowHeight) maxRowHeight = effectiveHeight
                } else {
                    totalLengthCm += maxRowHeight
                    currentLineWidth = effectiveWidth
                    maxRowHeight = effectiveHeight
                }
            }
        }
        totalLengthCm += maxRowHeight + 15.0 // Add 15cm buffer for squaring and grain alignment
        val totalMeters = ((totalLengthCm / 100.0) * 100).roundToInt() / 100.0

        val notesEn = "Drafted with $SEAM_ALLOWANCE_CM cm seam allowance. Align all pieces with straight grain (warp direction). Cut interfacing for collar and plackets."
        val notesSw = "Imechorwa ikiwa na nyongeza ya sm $SEAM_ALLOWANCE_CM ya pindo. Panga vipande kulingana na mwelekeo wa nyuzi za kitambaa. Kata gundi/interfacing kwa ajili ya kola na mifereji ya vifungo."

        return DraftingResult(
            pieces = pieces,
            fabricWidthInches = fabricWidthInches,
            totalLengthRequiredMeters = maxOf(1.5, totalMeters),
            cuttingNotes = notesEn,
            swahiliCuttingNotes = notesSw
        )
    }

    private fun draftShirt(p: MeasurementProfile): List<PatternPiece> {
        val chestQuarter = p.chestBust / 4.0
        val frontWidth = chestQuarter + 3.5 // Ease
        val backWidth = chestQuarter + 2.5
        val bodiceLength = p.desiredGarmentLength.coerceAtLeast(p.backLength + 15.0)

        return listOf(
            PatternPiece(
                name = "Front Shirt Bodice (Left & Right)",
                swahiliName = "Kipande cha Mbele cha Shati (Kushoto na Kulia)",
                widthCm = frontWidth,
                heightCm = bodiceLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 2,
                matchingNotches = listOf("Armhole Notch", "Side Seam Notch", "Center Front Placket"),
                description = "Includes button placket extension and curved armhole"
            ),
            PatternPiece(
                name = "Back Shirt Bodice (On Fold)",
                swahiliName = "Kipande cha Nyuma cha Shati (Pindo)",
                widthCm = backWidth,
                heightCm = bodiceLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 1,
                cutOnFold = true,
                matchingNotches = listOf("Side Seam Notch", "Yoke Seam Notch"),
                description = "Cut on straight fold with center box pleat option"
            ),
            PatternPiece(
                name = "Double Shirt Yoke",
                swahiliName = "Yoke ya Mabega ya Nyuma",
                widthCm = p.shoulderWidth + 2.0,
                heightCm = 14.0,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 2,
                cutOnFold = true,
                description = "Cut 2 for self-lined shoulder reinforcement"
            ),
            PatternPiece(
                name = "Sleeves (Pair)",
                swahiliName = "Mikono ya Shati (Jozi)",
                widthCm = p.armCircumference + 8.0,
                heightCm = p.sleeveLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 2,
                matchingNotches = listOf("Crown Notch", "Front Pitch Notch"),
                description = "Tapered sleeve with placket slit and cuff pleats"
            ),
            PatternPiece(
                name = "Collar & Collar Stand",
                swahiliName = "Kola na Msingi wa Kola (Stand)",
                widthCm = p.neck + 4.0,
                heightCm = 9.0,
                seamAllowanceCm = 1.0,
                cuttingQuantity = 2,
                description = "Interfaced collar and stand band with button tab"
            ),
            PatternPiece(
                name = "Sleeve Cuffs",
                swahiliName = "Kafu za Mikono",
                widthCm = 26.0,
                heightCm = 7.5,
                seamAllowanceCm = 1.0,
                cuttingQuantity = 2,
                description = "Interfaced rectangular cuffs with rounded edge"
            )
        )
    }

    private fun draftDress(p: MeasurementProfile): List<PatternPiece> {
        val bustQuarter = p.chestBust / 4.0 + 2.0
        val waistQuarter = p.waist / 4.0 + 2.5 // Dart allowance
        val hipQuarter = p.hips / 4.0 + 3.0
        val bodiceLength = p.backLength
        val skirtLength = (p.desiredGarmentLength - bodiceLength).coerceAtLeast(50.0)

        return listOf(
            PatternPiece(
                name = "Front Bodice with French Darts",
                swahiliName = "Kipande cha Mbele cha Kifuani chenye Dati",
                widthCm = bustQuarter,
                heightCm = bodiceLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 1,
                cutOnFold = true,
                matchingNotches = listOf("Bust Apex Point", "Side Dart Notch", "Waist Notch"),
                description = "Fitted bodice with bust shaping and neckline contouring"
            ),
            PatternPiece(
                name = "Back Bodice (Left & Right)",
                swahiliName = "Kipande cha Mgongoni (Kushoto na Kulia)",
                widthCm = bustQuarter - 1.0,
                heightCm = bodiceLength,
                seamAllowanceCm = 2.0, // Extra for zipper
                cuttingQuantity = 2,
                matchingNotches = listOf("Zipper Stop Notch", "Waist Notch"),
                description = "Includes 2cm center-back zipper allowance"
            ),
            PatternPiece(
                name = "Flared / Peplum Skirt Front",
                swahiliName = "Sketi ya Mbele yenye Muundo wa Mawimbi",
                widthCm = hipQuarter * 1.4,
                heightCm = skirtLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 1,
                cutOnFold = true,
                description = "Graceful A-line flare with hem sweep"
            ),
            PatternPiece(
                name = "Flared Skirt Back Panels",
                swahiliName = "Sketi ya Nyuma (Vipande 2)",
                widthCm = hipQuarter * 1.4,
                heightCm = skirtLength,
                seamAllowanceCm = 2.0,
                cuttingQuantity = 2,
                description = "Back skirt panels with center zipper extension"
            ),
            PatternPiece(
                name = "Bishop Sleeves",
                swahiliName = "Mikono ya Gauni yenye Pindo Nadhifu",
                widthCm = p.armCircumference + 12.0,
                heightCm = p.sleeveLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 2,
                description = "Gathered at cuff for volume"
            ),
            PatternPiece(
                name = "Bodice Neckline Facings",
                swahiliName = "Lining ya Shingo",
                widthCm = p.shoulderWidth / 2.0,
                heightCm = 8.0,
                seamAllowanceCm = 1.0,
                cuttingQuantity = 2,
                description = "Clean finish interior neckline facing"
            )
        )
    }

    private fun draftTrousers(p: MeasurementProfile): List<PatternPiece> {
        val hipQuarter = p.hips / 4.0 + 2.0
        val crotchDepth = (p.outseam - p.inseam).coerceAtLeast(24.0)
        val legLength = p.outseam

        return listOf(
            PatternPiece(
                name = "Front Trouser Leg (Pair)",
                swahiliName = "Mguu wa Mbele wa Suruali (Jozi)",
                widthCm = hipQuarter + 4.0,
                heightCm = legLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 2,
                matchingNotches = listOf("Knee Notch", "Inseam Notch", "Fly Notch"),
                description = "Front leg with slanted pocket opening and knee balance"
            ),
            PatternPiece(
                name = "Back Trouser Leg (Pair)",
                swahiliName = "Mguu wa Nyuma wa Suruali (Jozi)",
                widthCm = hipQuarter + 8.5, // Extended back crotch
                heightCm = legLength + 3.0, // Back rise pitch
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 2,
                matchingNotches = listOf("Knee Notch", "Inseam Balance", "Seat Notch"),
                description = "Back leg with extended seat curve and back waist darts"
            ),
            PatternPiece(
                name = "Contoured Waistband",
                swahiliName = "Mkanda wa Kiuno cha Suruali",
                widthCm = p.trouserWaist + 8.0,
                heightCm = 9.0,
                seamAllowanceCm = 1.0,
                cuttingQuantity = 1,
                description = "Curved waistband with extension for hook and eye closure"
            ),
            PatternPiece(
                name = "Pocket Bags & Facing",
                swahiliName = "Mifuko ya Ndani na Nje",
                widthCm = 20.0,
                heightCm = 30.0,
                seamAllowanceCm = 1.0,
                cuttingQuantity = 4,
                description = "Deep trouser pockets with reinforcement facing"
            ),
            PatternPiece(
                name = "Fly Shield & Underlap",
                swahiliName = "Kinga ya Zipu (Fly Guard)",
                widthCm = 6.0,
                heightCm = crotchDepth * 0.75,
                seamAllowanceCm = 1.0,
                cuttingQuantity = 2,
                description = "Zipper shield and fly facing"
            )
        )
    }

    private fun draftTraditional(p: MeasurementProfile): List<PatternPiece> {
        // Authentic East African / Tanzanian Kitenge Tunic or Kanzu cut
        val bodyWidth = (p.chestBust / 2.0) + 8.0
        val tunicLength = p.desiredGarmentLength.coerceAtLeast(115.0)

        return listOf(
            PatternPiece(
                name = "Traditional Tunic Main Body (Front & Back on Fold)",
                swahiliName = "Mwili Mkuu wa Nguo ya Asili (Mbele na Nyuma)",
                widthCm = bodyWidth,
                heightCm = tunicLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 1,
                cutOnFold = true,
                description = "Classic straight drape body panel with neckline slit and embroidery frame"
            ),
            PatternPiece(
                name = "Side Body Gussets (Vipande vya Pembeni)",
                swahiliName = "Vipande vya Pembeni (Gussets)",
                widthCm = 16.0,
                heightCm = tunicLength * 0.7,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 2,
                description = "Trapezoid inserts for full ease and dramatic flare"
            ),
            PatternPiece(
                name = "Straight Traditional Sleeves",
                swahiliName = "Mikono Iliyonyooka",
                widthCm = p.armCircumference + 10.0,
                heightCm = p.sleeveLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 2,
                description = "Square-set comfortable sleeve with contrast Kitenge border cuff"
            ),
            PatternPiece(
                name = "Embroidered Neckline Placket & Collar",
                swahiliName = "Urembo wa Shingoni na Kola ya Kiasili",
                widthCm = 12.0,
                heightCm = 42.0,
                seamAllowanceCm = 1.0,
                cuttingQuantity = 1,
                description = "Reinforced center chest placket ready for fine embroidery work"
            ),
            PatternPiece(
                name = "Underarm Diamond Gussets",
                swahiliName = "Vipande vya Kwapani (Gussets)",
                widthCm = 12.0,
                heightCm = 12.0,
                seamAllowanceCm = 1.0,
                cuttingQuantity = 2,
                description = "Traditional 45-degree diamond inserts for uninhibited arm movement"
            )
        )
    }

    private fun draftSkirt(p: MeasurementProfile): List<PatternPiece> {
        val waistQuarter = p.waist / 4.0 + 2.0
        val hipQuarter = p.hips / 4.0 + 2.5
        val skirtLength = p.desiredGarmentLength.coerceAtLeast(65.0)

        return listOf(
            PatternPiece(
                name = "Front Skirt Panel",
                swahiliName = "Kipande cha Mbele cha Sketi",
                widthCm = hipQuarter * 1.25,
                heightCm = skirtLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 1,
                cutOnFold = true,
                description = "Front panel with waist darts and gentle taper or flare"
            ),
            PatternPiece(
                name = "Back Skirt Panels (Left & Right)",
                swahiliName = "Vipande vya Nyuma vya Sketi",
                widthCm = hipQuarter * 1.25,
                heightCm = skirtLength,
                seamAllowanceCm = 2.0,
                cuttingQuantity = 2,
                matchingNotches = listOf("Vent Notch", "Zipper Notch"),
                description = "Back panels with walking vent/slit allowance and zipper seam"
            ),
            PatternPiece(
                name = "Straight Waistband",
                swahiliName = "Mkanda wa Sketi",
                widthCm = p.waist + 6.0,
                heightCm = 8.0,
                seamAllowanceCm = 1.0,
                cuttingQuantity = 1,
                description = "Fitted waistband with 3cm overlap"
            )
        )
    }

    private fun draftSuit(p: MeasurementProfile): List<PatternPiece> {
        val chestQuarter = p.chestBust / 4.0 + 4.5 // Tailored ease
        val jacketLength = p.backLength + 28.0

        return listOf(
            PatternPiece(
                name = "Suit Front Forepart (Pair)",
                swahiliName = "Mbele ya Koti la Suti (Jozi)",
                widthCm = chestQuarter + 3.0,
                heightCm = jacketLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 2,
                description = "Includes chest canvas shaping, roll line for lapel, and welt pockets"
            ),
            PatternPiece(
                name = "Suit Side Body Panel (Pair)",
                swahiliName = "Kipande cha Ubavu wa Koti",
                widthCm = 18.0,
                heightCm = jacketLength - 6.0,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 2,
                description = "Side silhouette contouring between front and back"
            ),
            PatternPiece(
                name = "Suit Back Panel (Pair)",
                swahiliName = "Mgongo wa Koti la Suti",
                widthCm = chestQuarter - 1.0,
                heightCm = jacketLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 2,
                description = "Center-back seam shaped to spine with double vent option"
            ),
            PatternPiece(
                name = "Two-Piece Sleeves (Top & Under Sleeve)",
                swahiliName = "Mikono ya Koti yenye Vipande Viwili",
                widthCm = p.armCircumference + 9.0,
                heightCm = p.sleeveLength,
                seamAllowanceCm = SEAM_ALLOWANCE_CM,
                cuttingQuantity = 4, // 2 top + 2 under
                description = "Classic tailored two-piece pitched sleeve"
            ),
            PatternPiece(
                name = "Notched Lapel & Collar",
                swahiliName = "Kola ya Koti (Lapel)",
                widthCm = 24.0,
                heightCm = 12.0,
                seamAllowanceCm = 1.0,
                cuttingQuantity = 2,
                description = "Upper collar and canvas facing for structured lapel roll"
            )
        )
    }
}
