import androidx.lifecycle.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {


    private val _ownerList = MutableStateFlow(listOf<CarOwner>())
    val ownerList: StateFlow<List<CarOwner>> = _ownerList.asStateFlow()

    private val _selected = MutableStateFlow<Int?>(null)
    val selected: StateFlow<Int?> = _selected.asStateFlow()

    private val _errorMessage = MutableStateFlow<String>("")
    val resultMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        _ownerList.value = listOf(
            CarOwner("Roli", "VW Golf", "677929"),
            CarOwner("Andy", "Ford", "289090"),
            CarOwner("Jessy", "Mazda", "782035"),
            CarOwner("Mami", "Audi", "460104"),
            CarOwner("Coci ", "VW Polo", "191262"),
            CarOwner("Randy", "", "592006"),
            CarOwner("Robi", "Toyota", "536428"),
            CarOwner("Angela", "", "69704"),// BS
            CarOwner("Stefan", "", "935570"),
            CarOwner("Jesse", "", "261864"),
        )
    }

    fun selectItem(index: Int) {
        _selected.value = index
    }

    suspend fun confirmReservation() {
        _loading.value = true

        if (selected.value == null) return
        val client = HttpClient()
        try {
            val response = client.get("http://192.168.1.221:8080/parkreservation") {
                url {
                    parameters.append("plateNumber", ownerList.value[selected.value!!].plateNumber)
                }
            }
            if (response.status.value in 200..299) {
                _errorMessage.value =
                    "${ownerList.value[selected.value!!].name} erfolgreich registriert!"
            } else {
                _errorMessage.value = "Registrierung fehlgeschlagen!"
            }
        } catch (e: Exception) {
            _errorMessage.value = e.message.toString()
        }

        _loading.value = false


    }
}

class CarOwner(val name: String, val carType: String, val plateNumber: String) {}