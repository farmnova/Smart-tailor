package com.example.util

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    SWAHILI("sw", "Kiswahili");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code == code } ?: ENGLISH
    }
}

object LocalizationStrings {
    fun t(key: String, language: AppLanguage): String {
        val strings = if (language == AppLanguage.SWAHILI) swahiliStrings else englishStrings
        return strings[key] ?: englishStrings[key] ?: key
    }

    private val englishStrings = mapOf(
        "app_title" to "Smart Tailoring AI",
        "app_subtitle" to "Atelier & Custom Fashion Studio",
        "nav_home" to "Studio",
        "nav_measure" to "Measurements",
        "nav_design" to "Design AI",
        "nav_fit" to "Virtual Fit",
        "nav_patterns" to "Patterns",
        "nav_sewing" to "Sewing Guide",
        "nav_fabric" to "Fabrics & Cost",
        "nav_projects" to "Projects",
        
        // Home
        "welcome_back" to "Habari! Welcome to Smart Tailor",
        "tagline" to "AI-powered bespoke tailoring, digital pattern drafting, fabric estimation & fitting",
        "quick_actions" to "Studio Quick Actions",
        "new_measurement" to "Capture Measurements",
        "design_garment" to "Design Garment",
        "cost_calculator" to "Cost Calculator",
        "recent_projects" to "Active Tailoring Projects",
        "atelier_metrics" to "Atelier Overview",
        "role_customer" to "Customer",
        "role_tailor" to "Professional Tailor",
        "role_designer" to "Fashion Designer",
        "role_admin" to "Workshop Admin",
        
        // Measurements
        "measurements_title" to "Body Measurements & Calibration",
        "measurement_disclaimer" to "Privacy Notice: Photos are analyzed locally for calibration. We never infer exact measurements without your confirmed reference scale.",
        "calibration_mode" to "Reference Scale Calibration",
        "standard_card_ref" to "Known Reference (ID/Credit Card: 8.56 cm)",
        "ruler_ref" to "Standard Metric Tape / Scale",
        "manual_measure" to "Manual Form Entry",
        "confidence_level" to "Calibration Confidence",
        "unit_metric" to "Centimeters (cm)",
        "unit_imperial" to "Inches (in)",
        "save_profile" to "Save Measurement Profile",
        "height" to "Full Height",
        "shoulder_width" to "Shoulder Width",
        "chest_bust" to "Chest / Bust",
        "waist" to "Natural Waist",
        "hips" to "Hip Circumference",
        "neck" to "Neck Circumference",
        "sleeve_length" to "Sleeve Length",
        "back_length" to "Back Length",
        "garment_length" to "Desired Garment Length",
        "trouser_waist" to "Trouser Waist",
        "inseam" to "Inseam Length",
        "outseam" to "Outseam (Length)",
        "thigh" to "Thigh Circumference",
        "leg_opening" to "Leg Opening / Ankle",

        // Design Studio
        "design_title" to "AI Fashion Design Studio",
        "describe_garment" to "Describe your garment vision...",
        "style_category" to "Garment Category",
        "category_traditional" to "African / Traditional (Kitenge / Kanga / Dashiki)",
        "category_shirt" to "Shirt / Top",
        "category_dress" to "Dress / Gown",
        "category_trouser" to "Trousers / Slacks",
        "category_suit" to "Suit / Blazer",
        "category_skirt" to "Skirt",
        "category_uniform" to "Formal / Uniform",
        "silhouette" to "Silhouette & Cut",
        "collar_type" to "Collar Style",
        "sleeve_style" to "Sleeve Style",
        "embellishments" to "Embellishments & Accents",
        "generate_design" to "Generate Design with Gemini",
        "refine_design" to "Refine with AI Consultation",
        
        // Virtual Fit
        "fitting_title" to "Virtual Fitting & Visualization",
        "visual_approximation_notice" to "Visual Approximation: Previews simulate drape and style; final fit relies on validated pattern measurements.",
        "approve_design" to "Approve Design for Patterning",
        "request_revision" to "Request Revision",
        "front_view" to "Front View",
        "side_view" to "Side View",
        "pattern_preview" to "2D Seam Overlay",

        // Sewing & Patterns
        "pattern_title" to "Geometric Pattern Engine",
        "pattern_disclaimer" to "Drafted using mathematical tailoring rules with 1.5 cm seam allowances and grainline markings.",
        "cutting_layout" to "Fabric Cutting Layout",
        "calibration_square" to "5 cm Print Calibration Square",
        "print_export" to "Export Pattern Blueprint",
        "sewing_instructions" to "Step-by-Step Sewing Guide",
        "beginner" to "Beginner",
        "intermediate" to "Intermediate",
        "advanced" to "Master Tailor",

        // Cost
        "fabric_calculator_title" to "Fabric & Cost Quotation",
        "fabric_type" to "Fabric Material",
        "fabric_width" to "Bolt Width",
        "meterage_required" to "Estimated Fabric Required",
        "currency_tzs" to "TZS (Tanzanian Shilling)",
        "total_cost_est" to "Estimated Total Production Cost",
        "suggested_price" to "Suggested Retail / Customer Quote"
    )

    private val swahiliStrings = mapOf(
        "app_title" to "Smart Tailoring AI",
        "app_subtitle" to "Studio ya Kisasa ya Kushona na Kubuni Nguo",
        "nav_home" to "Nyumbani",
        "nav_measure" to "Vipimo",
        "nav_design" to "Ubunifu AI",
        "nav_fit" to "Kujaribu Nguo",
        "nav_patterns" to "Mifumo ya Nguo",
        "nav_sewing" to "Mwongozo wa Ushonaji",
        "nav_fabric" to "Vitambaa & Gharama",
        "nav_projects" to "Miradi",

        // Home
        "welcome_back" to "Karibu kwenye Smart Tailor!",
        "tagline" to "Mfumo mahiri wa AI kwa mafundi cherehani, wabunifu na wateja. Pima, buni, na kata nguo kitaalamu.",
        "quick_actions" to "Vitendo vya Haraka",
        "new_measurement" to "Chukua Vipimo Vipya",
        "design_garment" to "Buni Nguo Mpya",
        "cost_calculator" to "Kokotoa Gharama",
        "recent_projects" to "Miradi ya Ushonaji Inayoendelea",
        "atelier_metrics" to "Muhtasari wa Karakana",
        "role_customer" to "Mteja",
        "role_tailor" to "Fundi Cherehani",
        "role_designer" to "Mbuni wa Mavazi",
        "role_admin" to "Msimamizi wa Karakana",

        // Measurements
        "measurements_title" to "Vipimo vya Mwili na Ulinganifu",
        "measurement_disclaimer" to "Usiri: Picha zinachakatwa kwa usalama bila kuweka hadharani. Vipimo vinathibitishwa na wewe kabla ya kukata kitambaa.",
        "calibration_mode" to "Kipimo cha Ulinganifu (Kadi au Rula)",
        "standard_card_ref" to "Kipimo cha Rejea (Kadi ya Benki: sm 8.56)",
        "ruler_ref" to "Kipimo cha Utepe / Rula ya kawaida",
        "manual_measure" to "Kujaza Vipimo kwa Mkono",
        "confidence_level" to "Kiwango cha Uhakika",
        "unit_metric" to "Sentimita (cm)",
        "unit_imperial" to "Inchi (in)",
        "save_profile" to "Hifadhi Vipimo",
        "height" to "Urefu Wote",
        "shoulder_width" to "Upana wa Mabega",
        "chest_bust" to "Kifua / Maziwa",
        "waist" to "Kiuno",
        "hips" to "Nyonga / Makalio",
        "neck" to "Mzingo wa Shingo",
        "sleeve_length" to "Urefu wa Mkono",
        "back_length" to "Urefu wa Mgongo",
        "garment_length" to "Urefu wa Nguo Inayotakiwa",
        "trouser_waist" to "Kiuno cha Suruali",
        "inseam" to "Upande wa Ndani wa Suruali",
        "outseam" to "Urefu Kamili wa Suruali",
        "thigh" to "Paja",
        "leg_opening" to "Upana wa Chini ya Mguu",

        // Design Studio
        "design_title" to "Studio ya Ubunifu wa Mavazi",
        "describe_garment" to "Eleza jinsi unavyotaka nguo yako iwe...",
        "style_category" to "Aina ya Nguo",
        "category_traditional" to "Mavazi ya Kitamaduni (Kitenge / Kanga / Dashiki / Kanzu)",
        "category_shirt" to "Shati / Fulana",
        "category_dress" to "Gauni / Nguo ya Kike",
        "category_trouser" to "Suruali",
        "category_suit" to "Suti / Koti",
        "category_skirt" to "Sketi",
        "category_uniform" to "Sare za Kazi / Sherehe",
        "silhouette" to "Umbo na Muundo",
        "collar_type" to "Mtindo wa Kola",
        "sleeve_style" to "Mtindo wa Mikono",
        "embellishments" to "Mapambo na Nakshi",
        "generate_design" to "Tengeneza Muundo na Gemini",
        "refine_design" to "Boresha Muundo na Ushauri wa AI",

        // Virtual Fit
        "fitting_title" to "Kujaribu Nguo Kidigitali",
        "visual_approximation_notice" to "Makadirio ya Muonekano: Muonekano huu ni kielelezo cha mtindo; vipimo halisi ndivyo vinavyotumika kukata.",
        "approve_design" to "Idhinisha Muundo Huu kwa Kukata",
        "request_revision" to "Omba Mabadiliko",
        "front_view" to "Mbele",
        "side_view" to "Pembeni",
        "pattern_preview" to "Mifumo ya Ushonaji",

        // Sewing & Patterns
        "pattern_title" to "Mifumo ya Kukata Nguo (Patterns)",
        "pattern_disclaimer" to "Imehesabiwa kwa kanuni sahihi za ushonaji ikiwa na nyongeza ya sm 1.5 ya pindo.",
        "cutting_layout" to "Mpangilio wa Kukatia Kitambaa",
        "calibration_square" to "Kipimo cha Kupima Karatasi (sm 5)",
        "print_export" to "Pakua Mchoro wa Kukatia",
        "sewing_instructions" to "Hatua kwa Hatua za Kushona",
        "beginner" to "Mwanzilishi",
        "intermediate" to "Kati",
        "advanced" to "Fundi Bingwa",

        // Cost
        "fabric_calculator_title" to "Kokotoa Kitambaa na Bei",
        "fabric_type" to "Aina ya Kitambaa",
        "fabric_width" to "Upana wa Kitambaa",
        "meterage_required" to "Mita Zinazohitajika",
        "currency_tzs" to "TZS (Shilingi ya Tanzania)",
        "total_cost_est" to "Makadirio ya Gharama Zote",
        "suggested_price" to "Bei ya Kumtoza Mteja"
    )
}
