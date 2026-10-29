package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.FashionConsultationResult
import com.example.ai.TailorAiService
import com.example.data.local.TailorDatabase
import com.example.data.model.FabricItem
import com.example.data.model.GarmentCategory
import com.example.data.model.GarmentDesign
import com.example.data.model.MeasurementProfile
import com.example.data.model.ProjectStatus
import com.example.data.model.SewingProject
import com.example.data.repository.TailorRepository
import com.example.engine.CostBreakdown
import com.example.engine.DraftingResult
import com.example.engine.FabricCostCalculator
import com.example.engine.MeasurementCalibrationEngine
import com.example.engine.PatternDraftingEngine
import com.example.engine.SewingGuide
import com.example.engine.SewingGuideEngine
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "TailorViewModel"

sealed interface AiTaskState {
    data object Idle : AiTaskState
    data class Loading(val message: String) : AiTaskState
    data class Success(val message: String, val result: Any? = null) : AiTaskState
    data class Error(val message: String) : AiTaskState
}

enum class UserRole(val label: String, val swahiliLabel: String) {
    CUSTOMER("Customer", "Mteja"),
    TAILOR("Professional Tailor", "Fundi Cherehani"),
    DESIGNER("Fashion Designer", "Mbuni wa Mavazi"),
    ADMIN("Workshop Admin", "Msimamizi wa Karakana")
}

class TailorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TailorRepository(TailorDatabase.getDatabase(application))
    private val aiService = TailorAiService()

    // Language & Role State
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _currentUserRole = MutableStateFlow(UserRole.TAILOR)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    // Database lists
    val profiles: StateFlow<List<MeasurementProfile>> = repository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val designs: StateFlow<List<GarmentDesign>> = repository.allDesigns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fabrics: StateFlow<List<FabricItem>> = repository.allFabrics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<SewingProject>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently Selected Working Entities
    private val _selectedProfile = MutableStateFlow<MeasurementProfile?>(null)
    val selectedProfile: StateFlow<MeasurementProfile?> = _selectedProfile.asStateFlow()

    private val _selectedDesign = MutableStateFlow<GarmentDesign?>(null)
    val selectedDesign: StateFlow<GarmentDesign?> = _selectedDesign.asStateFlow()

    private val _selectedFabric = MutableStateFlow<FabricItem?>(null)
    val selectedFabric: StateFlow<FabricItem?> = _selectedFabric.asStateFlow()

    // Engines Computed State
    private val _draftingResult = MutableStateFlow<DraftingResult?>(null)
    val draftingResult: StateFlow<DraftingResult?> = _draftingResult.asStateFlow()

    private val _sewingGuide = MutableStateFlow<SewingGuide?>(null)
    val sewingGuide: StateFlow<SewingGuide?> = _sewingGuide.asStateFlow()

    private val _costBreakdown = MutableStateFlow<CostBreakdown?>(null)
    val costBreakdown: StateFlow<CostBreakdown?> = _costBreakdown.asStateFlow()

    // AI State
    private val _aiState = MutableStateFlow<AiTaskState>(AiTaskState.Idle)
    val aiState: StateFlow<AiTaskState> = _aiState.asStateFlow()

    private val _generatedConceptImage = MutableStateFlow<Bitmap?>(null)
    val generatedConceptImage: StateFlow<Bitmap?> = _generatedConceptImage.asStateFlow()

    private val _consultationResult = MutableStateFlow<FashionConsultationResult?>(null)
    val consultationResult: StateFlow<FashionConsultationResult?> = _consultationResult.asStateFlow()

    init {
        // Automatically select the first profile/design/fabric when available
        viewModelScope.launch {
            repository.allProfiles.collect { list ->
                if (_selectedProfile.value == null && list.isNotEmpty()) {
                    _selectedProfile.value = list.first()
                    recomputePatternsAndGuide()
                }
            }
        }
        viewModelScope.launch {
            repository.allDesigns.collect { list ->
                if (_selectedDesign.value == null && list.isNotEmpty()) {
                    _selectedDesign.value = list.first()
                    recomputePatternsAndGuide()
                }
            }
        }
        viewModelScope.launch {
            repository.allFabrics.collect { list ->
                if (_selectedFabric.value == null && list.isNotEmpty()) {
                    _selectedFabric.value = list.first()
                    recomputeCost()
                }
            }
        }
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun setUserRole(role: UserRole) {
        _currentUserRole.value = role
    }

    fun selectProfile(profile: MeasurementProfile) {
        _selectedProfile.value = profile
        recomputePatternsAndGuide()
    }

    fun selectDesign(design: GarmentDesign) {
        _selectedDesign.value = design
        recomputePatternsAndGuide()
    }

    fun selectFabric(fabric: FabricItem) {
        _selectedFabric.value = fabric
        recomputeCost()
    }

    fun saveMeasurementProfile(profile: MeasurementProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
            _selectedProfile.value = profile
            recomputePatternsAndGuide()
        }
    }

    fun calibrateFromScale(
        clientName: String,
        gender: String,
        cardPixelWidth: Float,
        bodyHeightPixels: Float,
        shoulderPixels: Float,
        chestPixels: Float,
        waistPixels: Float,
        hipPixels: Float
    ) {
        val calibrated = MeasurementCalibrationEngine.computeCalibratedMeasurements(
            clientName = clientName,
            gender = gender,
            detectedCardPixelWidth = cardPixelWidth,
            headToToePixels = bodyHeightPixels,
            shoulderPixels = shoulderPixels,
            chestPixels = chestPixels,
            waistPixels = waistPixels,
            hipPixels = hipPixels
        )
        _selectedProfile.value = calibrated
        viewModelScope.launch {
            repository.saveProfile(calibrated)
            recomputePatternsAndGuide()
        }
    }

    fun updateDesignApproval(approved: Boolean, revisionNotes: String = "") {
        val design = _selectedDesign.value ?: return
        viewModelScope.launch {
            repository.setDesignApproval(design.id, approved, revisionNotes)
            val updated = design.copy(isApproved = approved, revisionNotes = revisionNotes)
            _selectedDesign.value = updated
            recomputePatternsAndGuide()
        }
    }

    fun saveDesign(design: GarmentDesign) {
        viewModelScope.launch {
            repository.saveDesign(design)
            _selectedDesign.value = design
            recomputePatternsAndGuide()
        }
    }

    fun recomputePatternsAndGuide() {
        val p = _selectedProfile.value ?: return
        val d = _selectedDesign.value ?: return
        val fabricWidth = _selectedFabric.value?.widthInches ?: 44.0

        val drafting = PatternDraftingEngine.draftPattern(d, p, fabricWidth)
        _draftingResult.value = drafting

        val guide = SewingGuideEngine.generateGuide(d, p)
        _sewingGuide.value = guide

        recomputeCost()
    }

    fun recomputeCost(
        laborTzs: Double = 45000.0,
        markupPercent: Int = 35
    ) {
        val design = _selectedDesign.value ?: return
        val fabric = _selectedFabric.value
        val fabricWidth = fabric?.widthInches ?: 44.0
        val pricePerMeter = fabric?.pricePerMeterTzs ?: 15000.0

        val breakdown = FabricCostCalculator.calculateCost(
            category = design.category,
            fabricWidthInches = fabricWidth,
            pricePerMeterTzs = pricePerMeter,
            hasEmbroidery = design.embellishments.isNotBlank(),
            tailorLaborTzs = laborTzs,
            markupPercent = markupPercent
        )
        _costBreakdown.value = breakdown
    }

    fun createProjectFromCurrent(title: String, clientName: String, notes: String) {
        val profile = _selectedProfile.value ?: return
        val design = _selectedDesign.value ?: return
        val fabric = _selectedFabric.value ?: return
        val cost = _costBreakdown.value ?: return

        val project = SewingProject(
            projectTitle = title,
            clientName = clientName,
            profileId = profile.id,
            designId = design.id,
            fabricId = fabric.id,
            status = if (design.isApproved) ProjectStatus.DESIGN_APPROVED else ProjectStatus.MEASURED,
            fabricMetersRequired = cost.requiredMeters,
            fabricCostTzs = cost.fabricCostTzs,
            notionsCostTzs = cost.notionsCostTzs,
            embroideryCostTzs = cost.embroideryCostTzs,
            laborCostTzs = cost.laborCostTzs,
            totalCostTzs = cost.totalProductionCostTzs,
            sellingPriceTzs = cost.suggestedSellingPriceTzs,
            tailorNotes = notes
        )

        viewModelScope.launch {
            repository.saveProject(project)
        }
    }

    fun updateProjectStatus(projectId: String, status: ProjectStatus) {
        viewModelScope.launch {
            repository.updateProjectStatus(projectId, status)
        }
    }

    // AI Operations
    fun consultFashionAi(
        prompt: String,
        category: GarmentCategory,
        colors: String
    ) {
        viewModelScope.launch {
            _aiState.value = AiTaskState.Loading(
                if (_currentLanguage.value == AppLanguage.SWAHILI) "Gemini inachanganua muundo na kutoa ushauri..." else "Gemini is consulting tailoring aesthetics..."
            )
            val isSwahili = _currentLanguage.value == AppLanguage.SWAHILI
            val gender = _selectedProfile.value?.gender ?: "Unisex"

            val result = aiService.consultFashionDesign(
                userPrompt = prompt,
                category = category,
                preferredColors = colors,
                clientGender = gender,
                isSwahili = isSwahili
            )

            result.onSuccess { consultation ->
                _consultationResult.value = consultation
                _aiState.value = AiTaskState.Success("Consultation ready", consultation)
            }.onFailure { error ->
                Log.e(TAG, "AI consultation error", error)
                _aiState.value = AiTaskState.Error(error.localizedMessage ?: "AI consultation failed.")
            }
        }
    }

    fun generateGarmentImage(prompt: String) {
        viewModelScope.launch {
            _aiState.value = AiTaskState.Loading(
                if (_currentLanguage.value == AppLanguage.SWAHILI) "Inatengeneza picha ya muundo (inaweza kuchukua sekunde 30-60)..." else "Synthesizing garment concept (takes 30-60s)..."
            )

            val result = aiService.generateGarmentImage(prompt)
            result.onSuccess { bitmap ->
                _generatedConceptImage.value = bitmap
                _aiState.value = AiTaskState.Success("Garment image synthesized successfully")
            }.onFailure { error ->
                Log.e(TAG, "Image generation error", error)
                _aiState.value = AiTaskState.Error(error.localizedMessage ?: "Image generation failed.")
            }
        }
    }

    fun analyzeFabricPhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            _aiState.value = AiTaskState.Loading(
                if (_currentLanguage.value == AppLanguage.SWAHILI) "Inachambua sifa za kitambaa..." else "Analyzing fabric weave & properties..."
            )
            val isSwahili = _currentLanguage.value == AppLanguage.SWAHILI
            val result = aiService.analyzeFabricImage(bitmap, isSwahili)

            result.onSuccess { description ->
                _aiState.value = AiTaskState.Success(description)
            }.onFailure { error ->
                _aiState.value = AiTaskState.Error(error.localizedMessage ?: "Fabric analysis failed.")
            }
        }
    }

    fun clearAiError() {
        _aiState.value = AiTaskState.Idle
    }
}
