package com.ryen.sunnah_alhadi



/*                                TO DO
*  Add Scheduling feature using WorkManager
*  Add Export feature
*  Add Disclaimer feature
*  Add Onboarding feature
*  Add Search feature                                    */




/*// Usage Example of useCase in ViewModel
class HomeViewModel(
    private val getFeaturedCategoriesUseCase: GetFeaturedCategoriesUseCase,
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase,
    private val getSunnahOfTheDayUseCase: GetSunnahOfTheDayUseCase
) : ViewModel() {

    // Real-time user preferences
    val userPreferences = getUserPreferencesFlowUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    private val _featuredCategories = MutableStateFlow<List<Category>>(emptyList())
    val featuredCategories = _featuredCategories.asStateFlow()

    private val _sunnahOfTheDay = MutableStateFlow<Sunnah?>(null)
    val sunnahOfTheDay = _sunnahOfTheDay.asStateFlow()

    init {
        loadFeaturedCategories()
        loadSunnahOfTheDay()
    }

    private fun loadFeaturedCategories() {
        viewModelScope.launch {
            _featuredCategories.value = getFeaturedCategoriesUseCase()
        }
    }

    private fun loadSunnahOfTheDay() {
        viewModelScope.launch {
            _sunnahOfTheDay.value = getSunnahOfTheDayUseCase()
        }
    }
}*/


