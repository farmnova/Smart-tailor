package com.example.engine

import com.example.data.model.GarmentCategory
import com.example.data.model.GarmentDesign
import com.example.data.model.MeasurementProfile
import com.example.data.model.SewingStep

data class SewingGuide(
    val garmentTitle: String,
    val skillLevel: String,
    val swahiliSkillLevel: String,
    val toolsRequired: List<String>,
    val swahiliToolsRequired: List<String>,
    val fabricPreparation: String,
    val swahiliFabricPreparation: String,
    val steps: List<SewingStep>,
    val qualityChecklist: List<String>,
    val swahiliQualityChecklist: List<String>
)

object SewingGuideEngine {
    fun generateGuide(design: GarmentDesign, profile: MeasurementProfile): SewingGuide {
        val toolsEn = listOf(
            "Fabric shears & thread snips",
            "Tailor's chalk / heat-erasable marking pen",
            "Fine glass-head pins (0.5mm)",
            "Steam iron & tailor's ham",
            "Sewing machine with size 80/12 or 90/14 needle",
            "Overlocker / serger (or zig-zag foot for edge finishing)",
            "Point turner & wooden seam roller"
        )
        val toolsSw = listOf(
            "Mikasi mikali ya kitambaa na mikasi midogo ya nyuzi",
            "Chaki ya fundi cherehani au kalamu inayofutika kwa pasi",
            "Pini ndogo safi za kushonea",
            "Pasi ya mvuke na mto wa kupigia pasi (tailor's ham)",
            "Cherehani yenye sindano nambari 14 au 16",
            "Mashine ya overlock (au mguu wa zig-zag wa kuzuia nyuzi kutanuka)",
            "Kijiti cha kunyooshea pembe za kola"
        )

        val prepEn = "Pre-wash/pre-shrink fabric in lukewarm water to prevent shrinkage after sewing. Press thoroughly on reverse side. Verify straight grain along selvedge."
        val prepSw = "Loweka kitambaa kwenye maji ya vuguvugu na ukikaushe kivulini ili kisije kikakauka baada ya kushonwa. Kinyooshe vizuri kwa pasi upande wa nyuma kabla ya kukata."

        val steps = when (design.category) {
            GarmentCategory.SHIRT -> getShirtSteps()
            GarmentCategory.DRESS -> getDressSteps()
            GarmentCategory.TROUSERS -> getTrouserSteps()
            GarmentCategory.TRADITIONAL_AFRICAN -> getTraditionalSteps()
            GarmentCategory.SUIT -> getSuitSteps()
            GarmentCategory.SKIRT -> getSkirtSteps()
            GarmentCategory.UNIFORM -> getShirtSteps()
        }

        val qcEn = listOf(
            "All seams pressed open or directed towards center back.",
            "Collar points symmetrical with crisp corners.",
            "Buttonholes evenly spaced and aligned with buttons.",
            "Hem is level and blind-stitched or cleanly topstitched.",
            "No puckering on armholes or curved necklines."
        )
        val qcSw = listOf(
            "Pindo zote zimenyooshwa vizuri kwa pasi.",
            "Ncha za kola zinalingana sawa pande zote mbili.",
            "Matundu ya vifungo yameachana kwa umbali unaolingana.",
            "Pindo la chini limekaa sawa bila kupinda au kuvutana.",
            "Hakuna makunyanzi kwapani wala shingoni."
        )

        return SewingGuide(
            garmentTitle = design.title,
            skillLevel = "Intermediate to Advanced",
            swahiliSkillLevel = "Fundi wa Kati hadi Bingwa",
            toolsRequired = toolsEn,
            swahiliToolsRequired = toolsSw,
            fabricPreparation = prepEn,
            swahiliFabricPreparation = prepSw,
            steps = steps,
            qualityChecklist = qcEn,
            swahiliQualityChecklist = qcSw
        )
    }

    private fun getShirtSteps(): List<SewingStep> = listOf(
        SewingStep(
            stepNumber = 1,
            title = "Apply Interfacing to Collar & Plackets",
            swahiliTitle = "Bandika Gundi (Interfacing) kwenye Kola na Mifereji",
            instruction = "Fuse lightweight woven interfacing to the undercollar, collar stand, and front button plackets using medium steam iron.",
            swahiliInstruction = "Bandika gundi nyepesi ya nguo kwenye upande wa ndani wa kola, stand ya kola, na mifereji ya vifungo kwa kutumia pasi ya mvuke.",
            stitchType = "Fusible Heat Press",
            seamAllowance = "0.5 cm margin",
            checkpoint = "Ensure no air bubbles or wrinkles under the fusible fabric."
        ),
        SewingStep(
            stepNumber = 2,
            title = "Construct Front Button Plackets",
            swahiliTitle = "Tengeneza Mifereji ya Vifungo ya Mbele",
            instruction = "Fold placket extensions along marked lines, press, and topstitch 2mm from inner folded edge.",
            swahiliInstruction = "Kunja upande wa mbele kulingana na alama za chaki, piga pasi, kisha shona stichi nadhifu ya milimita 2 kutoka pembeni.",
            stitchType = "Edge Stitch (2.0mm)",
            seamAllowance = "Folded 3 cm placket",
            checkpoint = "Measure placket width across entire front length to ensure uniform 3.0 cm width."
        ),
        SewingStep(
            stepNumber = 3,
            title = "Attach Yoke using Burrito Method",
            swahiliTitle = "Unganisha Yoke ya Mgongoni kwa Njia ya 'Burrito'",
            instruction = "Sandwich the back bodice between the inner and outer yoke pieces. Stitch, press upward, and roll shirt body to encase shoulder seams with zero exposed raw edges.",
            swahiliInstruction = "Weka kipande cha nyuma katikati ya vipande viwili vya yoke. Shona, nyoosha kwa pasi, kisha unganisha mabega ya mbele bila kuacha uzi wowote unaoonekana ndani.",
            stitchType = "Straight Stitch (2.5mm)",
            seamAllowance = "1.5 cm (5/8 in)",
            checkpoint = "Verify shoulder lengths match front panels precisely."
        ),
        SewingStep(
            stepNumber = 4,
            title = "Assemble and Set the Shirt Collar",
            swahiliTitle = "Shona na Unganisha Kola ya Shati",
            instruction = "Stitch collar points with slightly shortened stitch (2.0mm) for reinforcement. Clip corners diagonally, turn right side out, press, and sandwich into the collar stand.",
            swahiliInstruction = "Shona ncha za kola kwa umakini mkubwa. Kata kona kidogo ili isiwe nene, geuza upande wa kulia, piga pasi, na uishone kwenye stand ya kola.",
            stitchType = "Straight Stitch (2.0mm)",
            seamAllowance = "1.0 cm",
            checkpoint = "Collar points must match right and left with exact identical angle."
        ),
        SewingStep(
            stepNumber = 5,
            title = "Set Sleeves Flat & Close Side Seams",
            swahiliTitle = "Weka Mikono na Shona Ubavu",
            instruction = "Align sleeve center notch to shoulder seam. Stitch sleeve cap into armhole. Finish with French seam or flat-felled seam along side and underarm in one continuous pass.",
            swahiliInstruction = "Panga alama ya katikati ya mkono ilingane na bega. Shona mkono kwenye kwapa, kisha unganisha ubavu na upande wa ndani wa mkono kwa pamoja.",
            stitchType = "Flat-felled / French Seam",
            seamAllowance = "1.5 cm",
            checkpoint = "Ensure underarm seam intersection crosses cleanly at a perfect cross."
        ),
        SewingStep(
            stepNumber = 6,
            title = "Attach Cuffs and Sew Hem",
            swahiliTitle = "Weka Kafu za Mikono na Pinda Chini",
            instruction = "Gather sleeve opening to fit cuff. Topstitch cuff band. Roll narrow curved shirt hem twice (6mm) and edgestitch.",
            swahiliInstruction = "Kusanya pindo la mkono ili lilingane na kafu. Shona kafu kwa urembo. Pinda chini ya shati pindo nyembamba ya milimita 6 kwa uzuri.",
            stitchType = "Topstitch & Narrow Hem",
            seamAllowance = "0.6 cm rolled hem",
            checkpoint = "Check cuff circumference fits customer wrist with 4cm ease."
        )
    )

    private fun getDressSteps(): List<SewingStep> = listOf(
        SewingStep(
            stepNumber = 1,
            title = "Sew French & Contour Bust Darts",
            swahiliTitle = "Shona Dati za Kifuani",
            instruction = "Fold darts right sides together, pinning from side seam to bust apex point. Stitch towards point, leaving thread tails to tie by hand (do not backstitch at apex). Press darts downward.",
            swahiliInstruction = "Panga na ubonyeze dati kuanzia ubavuni kuelekea kilele cha kifua. Shona kuelekea nchani bila kurudisha cherehani nyuma, kisha funga ncha kwa mkono. Piga pasi zielekee chini.",
            stitchType = "Straight Stitch (2.0mm)",
            seamAllowance = "Taper to zero at apex",
            checkpoint = "Check bust shape on dress form or client for natural contour without dimples."
        ),
        SewingStep(
            stepNumber = 2,
            title = "Join Shoulder Seams & Neckline Facing",
            swahiliTitle = "Unganisha Mabega na Lining ya Shingo",
            instruction = "Join front and back bodice shoulders. Apply reinforced facing to neckline, clip curves, understitch facing 2mm from seam to prevent rolling outward.",
            swahiliInstruction = "Unganisha mabega ya mbele na nyuma. Shona lining ya shingo, kata mikato midogo kwenye mistari ya mviringo, kisha piga stichi ya ndani (understitch) kuzuia lining isijitokeze nje.",
            stitchType = "Understitch & Straight Stitch",
            seamAllowance = "1.0 cm at neckline",
            checkpoint = "Neckline lays flat against the chest without gaping or pulling."
        ),
        SewingStep(
            stepNumber = 3,
            title = "Install Concealed Invisible Zipper",
            swahiliTitle = "Weka Zipu ya Ndani (Invisible Zipper) ya Mgongoni",
            instruction = "Uncurl zipper teeth with warm iron. Use invisible zipper foot to stitch zipper tape 2mm away from teeth from neckline to hip notch.",
            swahiliInstruction = "Nyoosha meno ya zipu kwa pasi ya joto wastani. Tumia mguu maalumu wa zipu ya ndani (invisible foot) kushona pembezoni mwa meno kuanzia shingoni hadi nyongani.",
            stitchType = "Concealed Zipper Stitch",
            seamAllowance = "2.0 cm center-back seam",
            checkpoint = "Zipper completely invisible when zipped up with teeth nestled inside seam."
        ),
        SewingStep(
            stepNumber = 4,
            title = "Attach Skirt to Bodice Waistline",
            swahiliTitle = "Unganisha Gauni Kwenye Kiuno",
            instruction = "Match side seams and waist center markings. Stitch waistline seam with stay tape reinforcement to prevent stretching over time. Press seam upward.",
            swahiliInstruction = "Panga ubavu wa sketi ulingane na wa kifua. Shona kiunoni ukitumia utepe mwembamba (stay tape) ili kiuno kisilegeze baadaye. Pasi ielekee juu.",
            stitchType = "Reinforced Waist Stitch",
            seamAllowance = "1.5 cm",
            checkpoint = "Waist seam matches horizontally across zipper closure with zero stagger."
        ),
        SewingStep(
            stepNumber = 5,
            title = "Hemming & Final Couture Pressing",
            swahiliTitle = "Kupinda Chini na Pasi ya Mwisho ya Hadhi ya Juu",
            instruction = "Hang garment on mannequin for 12 hours before marking hem to let bias settle. Blind-stitch hem by hand or machine with matching silk thread.",
            swahiliInstruction = "Tundika gauni kwenye henga kwa saa kadhaa kabla ya kupinda ili kitambaa kishuke vizuri. Shona pindo la chini kwa stichi isiyoonekana nje (blind stitch).",
            stitchType = "Blind Hemming Stitch",
            seamAllowance = "3.0 cm dress hem",
            checkpoint = "Verify hem distance from floor is identical 360 degrees around."
        )
    )

    private fun getTrouserSteps(): List<SewingStep> = listOf(
        SewingStep(
            stepNumber = 1,
            title = "Construct Slanted Front Pockets",
            swahiliTitle = "Tengeneza Mifuko ya Mbele ya Suruali",
            instruction = "Attach pocket bag facing to trouser front opening. Understitch, turn, press, and stitch lower pocket bag to form pocket pouch. Baste pocket top to waistline.",
            swahiliInstruction = "Unganisha mfuko kwenye mteremko wa mbele wa suruali. Shona understitch, geuza, piga pasi, na ufunge chini ya mfuko. Shona kwa mshono mpana kiunoni ili usisogee.",
            stitchType = "Straight Stitch & Overcast",
            seamAllowance = "1.0 cm",
            checkpoint = "Pocket lays completely flat against hip without pulling open."
        ),
        SewingStep(
            stepNumber = 2,
            title = "Sew Trouser Fly Front with Shield",
            swahiliTitle = "Shona Zipu ya Suruali yenye Kinga (Fly)",
            instruction = "Attach zipper to left fly extension. Stitch right fly shield to protect skin and underwear from zipper teeth. Topstitch classic J-curve fly on left front.",
            swahiliInstruction = "Weka zipu kwenye ukingo wa kushoto. Unganisha kinga ya zipu upande wa kulia. Shona stichi ya herufi 'J' kwa umaridadi upande wa mbele wa kushoto.",
            stitchType = "J-Curve Topstitch (3.0mm)",
            seamAllowance = "Standard Fly Width (3.5 cm)",
            checkpoint = "Fly opens and closes smoothly with zipper pull fully concealed."
        ),
        SewingStep(
            stepNumber = 3,
            title = "Sew Inseams and Crotch Curve",
            swahiliTitle = "Unganisha Upande wa Ndani wa Mguu na Msamba",
            instruction = "Stitch front to back along inseam, stretching back inseam slightly around knee. Join crotch curve with double reinforced stitch.",
            swahiliInstruction = "Shona upande wa ndani wa mguu kuanzia msambani hadi chini. Shona msamba kwa stichi mbili imara za kuzuia kupasuka.",
            stitchType = "Double Reinforced Straight Stitch",
            seamAllowance = "1.5 cm",
            checkpoint = "Test crotch tension to ensure full range of comfortable movement."
        ),
        SewingStep(
            stepNumber = 4,
            title = "Attach Tailored Waistband & Belt Loops",
            swahiliTitle = "Weka Mkanda wa Kiuno na Vishikizo vya Mkanda (Belt Loops)",
            instruction = "Fuse waistband with stiff canvas interfacing. Insert 6 prepared belt loops. Stitch waistband to trouser body, stitch in the ditch to secure inside facing.",
            swahiliInstruction = "Bandika gundi ngumu kwenye mkanda wa kiuno. Weka vishikizo 6 vya mkanda. Shona mkanda kwenye suruali na piga stichi ya siri ndani ya mfereji (stitch in the ditch).",
            stitchType = "Stitch in the Ditch",
            seamAllowance = "1.0 cm",
            checkpoint = "Waistband circumference exactly matches client trouser waist measurement."
        ),
        SewingStep(
            stepNumber = 5,
            title = "Blind Hemming Trouser Cuffs",
            swahiliTitle = "Pinda Chini ya Suruali",
            instruction = "Mark pant length with shoes on. Apply hem tape to raw edge, turn up 4cm, and blind hem cleanly with matching thread.",
            swahiliInstruction = "Pima urefu wa suruali mteja akiwa amevaa viatu. Weka utepe wa kuzuia nyuzi kutanuka, kunja sm 4 kwenda juu, na pinda kwa stichi isiyoonekana nje.",
            stitchType = "Blind Hem Stitch",
            seamAllowance = "4.0 cm tailored trouser hem",
            checkpoint = "Hem break rests gracefully on the instep of the shoe."
        )
    )

    private fun getTraditionalSteps(): List<SewingStep> = listOf(
        SewingStep(
            stepNumber = 1,
            title = "Prepare Neck Placket & Apply Embroidery Stabilizer",
            swahiliTitle = "Andaa Urembo wa Shingoni na Karatasi ya Nakshi",
            instruction = "Fuse tear-away or water-soluble stabilizer to the center chest placket. Trace intricate Tanzanian filigree or geometric motif.",
            swahiliInstruction = "Bandika karatasi maalumu ya nakshi (stabilizer) upande wa nyuma wa kifuani. Chora muundo wa nakshi za kitamaduni za Kitanzania au Kiswahili.",
            stitchType = "Satin Stitch & Free-Motion Embroidery",
            seamAllowance = "Edge border margin",
            checkpoint = "Stitch density must be uniform without causing fabric puckering."
        ),
        SewingStep(
            stepNumber = 2,
            title = "Execute Gold Metallic Zari / Thread Embroidery",
            swahiliTitle = "Piga Nakshi za Dhahabu / Uzi wa Urembo",
            instruction = "Embroider neckline frame and buttonhole area using lustrous gold metallic or contrasting cotton embroidery thread. Press from reverse side onto soft towel.",
            swahiliInstruction = "Shona nakshi za dhahabu au uzi wa rangi ya kuvutia kuzunguka shingo na kifuani. Piga pasi kwa nyuma juu ya taulo laini ili nakshi zisibonyezwe.",
            stitchType = "Couching & Satin Embroidery",
            seamAllowance = "N/A",
            checkpoint = "Embroidery stands proud with crisp dimensional luster."
        ),
        SewingStep(
            stepNumber = 3,
            title = "Attach Diamond Underarm Gussets",
            swahiliTitle = "Unganisha Vipande vya Kwapani (Gussets)",
            instruction = "Insert traditional 45-degree diamond gussets between the sleeve and body to provide historic East African ease and range of motion.",
            swahiliInstruction = "Weka vipande vya almasi kwapani vinavyounganisha mkono na mwili ili kutoa nafasi nzuri ya kukunja mikono na hewa safi.",
            stitchType = "French Seam",
            seamAllowance = "1.0 cm",
            checkpoint = "All four corners of diamond gusset meet neatly with zero puckers."
        ),
        SewingStep(
            stepNumber = 4,
            title = "Assemble Side Slits with Reinforced Bar Tacks",
            swahiliTitle = "Shona Mipasuo ya Pembeni (Side Slits)",
            instruction = "Finish lower side slits with decorative Kitenge border facing. Secure top of each slit with dense horizontal bar tack to prevent tearing.",
            swahiliInstruction = "Malizia mipasuo ya pembeni ukitumia kitambaa cha ukingo wa Kitenge. Shona bar-tack imara juu ya mpasuo kuzuia nguo isichanike wakati wa kuvaa.",
            stitchType = "Bar Tack & Topstitch",
            seamAllowance = "1.5 cm",
            checkpoint = "Both side slits are positioned at equal height from hem."
        )
    )

    private fun getSuitSteps(): List<SewingStep> = listOf(
        SewingStep(
            stepNumber = 1,
            title = "Pad-stitch Lapel Canvas & Chest Piece",
            swahiliTitle = "Shona Canvas ya Kifuani na Kola ya Koti",
            instruction = "Baste horsehair canvas to suit fronts. Pad-stitch lapel roll line by hand to create permanent natural roll without pressing crease.",
            swahiliInstruction = "Shona canvas ngumu ndani ya koti kwa stichi ndogo za mkono (pad stitch) ili kola ijikunje yenyewe kiasili bila kupigwa pasi kali.",
            stitchType = "Tailor Pad Stitching",
            seamAllowance = "Hand canvas tailoring",
            checkpoint = "Lapel rolls naturally over chest without stiffness."
        ),
        SewingStep(
            stepNumber = 2,
            title = "Craft Breast Welt Pocket & Flap Pockets",
            swahiliTitle = "Shona Mfuko wa Kifuani (Welt Pocket) na Mifuko ya Chini",
            instruction = "Cut and fold pocket welts with precision. Stitch welt to forepart, slash pocket opening to corners with sharp shears, turn, and press sharply.",
            swahiliInstruction = "Kata na ukunje mifuko ya welt kwa umakini mkubwa. Shona, pasua katikati kuelekea pembe nne, geuza ndani na piga pasi kali.",
            stitchType = "Precision Straight Stitch",
            seamAllowance = "0.7 cm pocket box",
            checkpoint = "Welt lips meet with zero gap and sharp square corners."
        ),
        SewingStep(
            stepNumber = 3,
            title = "Construct Pitched Two-Piece Sleeves & Vent",
            swahiliTitle = "Unganisha Mikono ya Koti na Mifereji ya Vifungo",
            instruction = "Ease top sleeve cap into armhole with sleeve roll header and shoulder pads to give masculine or structured couture silhouette.",
            swahiliInstruction = "Unganisha mkono wa koti wenye vipande viwili, weka pamba za mabega (shoulder pads) na kitambaa cha kuinua mkono (sleeve head).",
            stitchType = "Armhole Structured Seam",
            seamAllowance = "1.5 cm",
            checkpoint = "Sleeve hangs straight without twisting forward or backward."
        )
    )

    private fun getSkirtSteps(): List<SewingStep> = listOf(
        SewingStep(
            stepNumber = 1,
            title = "Sew Waist Darts & Back Vent",
            swahiliTitle = "Shona Dati za Kiunoni na Mpasuo wa Nyuma wa Sketi",
            instruction = "Stitch front and back waist darts. Prepare walking vent on back center seam with mitered corner finish.",
            swahiliInstruction = "Shona dati za kiunoni mbele na nyuma. Andaa mpasuo wa nyuma wa sketi wenye pembe nadhifu ya digrii 45.",
            stitchType = "Straight Stitch",
            seamAllowance = "2.0 cm center-back",
            checkpoint = "Walking vent underlap lays smooth without gaping."
        ),
        SewingStep(
            stepNumber = 2,
            title = "Insert Concealed Zipper & Attach Waistband",
            swahiliTitle = "Weka Zipu na Mkanda wa Sketi",
            instruction = "Insert invisible zipper on back seam. Stitch interfaced waistband to waistline, fold over inside, and stitch in the ditch.",
            swahiliInstruction = "Weka zipu ya siri nyuma. Shona mkanda wa kiuno uliowekwa gundi, kunja ndani na ushike kwa stichi ya siri.",
            stitchType = "Invisible Zipper & Waist Seam",
            seamAllowance = "1.0 cm",
            checkpoint = "Waistband sits snug on natural waistline."
        )
    )
}
