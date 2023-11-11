import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.BirdImage
import repository.BirdRepository

// holding the state of the application
data class BirdsUiState(
    val images: List<BirdImage> = emptyList(),
    val selectedCategory: String? = null
) {
    val categories = images.map { it.category }.toSet()
    val selectedImages = images.filter { it.category == selectedCategory }
}

class BirdsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BirdsUiState())
    private val birdRepository = BirdRepository()
    val uiState = _uiState.asStateFlow()

    init {
        updateImages()
    }

    override fun onCleared() {
        birdRepository.closeHttpClient()
    }

    fun selectCategory(category: String) {
        _uiState.update {
            it.copy(selectedCategory = category)
        }
    }

    // get the images and update the UI state
    private fun updateImages() {
        viewModelScope.launch {
            val images = birdRepository.getImages()
            _uiState.update {
                it.copy(images = images)
            }
        }
    }
}