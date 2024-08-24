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
            CarOwner("Roli", "VW Golf", "677929", "ZH"),
            CarOwner("Andy", "Ford", "289090", "ZH"),
            CarOwner("Jessy", "Mazda", "782035", "ZH"),
            CarOwner("Mami", "Audi", "460104", "ZH"),
            CarOwner("Coci ", "VW Polo", "191262", "ZH"),
            CarOwner("Randy", "Hyundai", "592006", "ZH"),
            CarOwner("Robi", "Toyota", "536428", "ZH"),
            CarOwner("Angela", "", "69704", "BS"),// BS
            CarOwner("Stefan", "", "935570", "ZH"),
            CarOwner("Jesse", "", "261864", "ZH"),
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
            var host = "http://raspberrypi-1.tail3e513.ts.net:8080/parkreservation"
            var localhost = "http://192.168.1.221:8080/parkreservation"

            val response = client.get(host) {
                url {
                    parameters.append("plateNumber", ownerList.value[selected.value!!].plateNumber)
                    parameters.append("canton", ownerList.value[selected.value!!].canton)
                }
            }
            if (response.status.value in 200..299) {
                _errorMessage.value =
                    "${ownerList.value[selected.value!!].owner} erfolgreich registriert!"
            } else {
                _errorMessage.value = "Registrierung fehlgeschlagen!"
            }
        } catch (e: Exception) {
            _errorMessage.value = e.message.toString()
        }

        _loading.value = false
    }

}

class CarOwner(val owner: String, val carType: String, val plateNumber: String, val canton: String) {}