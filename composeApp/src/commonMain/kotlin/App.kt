import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel: AppViewModel = viewModel { AppViewModel() }

        Scaffold(
            modifier = Modifier.background(Color.White).padding(20.dp),
            bottomBar = {
                ReservationBottomBar(
                    viewModel.resultMessage.collectAsState().value,
                    viewModel.loading.collectAsState().value,
                    viewModel.selected.collectAsState().value
                ) {
                    CoroutineScope(Dispatchers.Default).launch {
                        viewModel.confirmReservation()
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                LazyVerticalGrid (
                    columns = GridCells.Adaptive(minSize = 120.dp)
                ) {
                    val ownerList = viewModel.ownerList.value
                    items(ownerList.size) { ownerIndex ->
                        CarOwnerItem(
                            ownerList[ownerIndex],
                            viewModel.selected.collectAsState().value == ownerIndex
                        ) { viewModel.selectItem(ownerIndex) }
                    }
                }
            }
        }

    }
}

@Composable
private fun ReservationBottomBar(
    resultMessage: String,
    loading: Boolean,
    selected: Int?,
    confirmReservation: () -> Unit
) {
    Column() {
        Text(
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
            text = resultMessage,
            maxLines = 1,
        )

        var buttonColor = if (getPlatform() == "Android") {
            Color(0xFFcae3d5) //Green
        } else{
            Color(0xFFfadef9) //
        }

        Button(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = buttonColor,
                contentColor = Color.Black
            ),
            onClick = {
                confirmReservation()
            },
            enabled = !loading && selected != null,
            content = {
                if (loading) {
                    CircularProgressIndicator(
                        Modifier.then(Modifier.size(30.dp)),
                        color = Color(0xFF68ad86)
                    )
                } else {
                    Text("Parking bestätigen")
                }
            })


}
}

@Composable
fun CarOwnerItem(car: CarOwner, selected: Boolean, selectItem: () -> Unit) {
    Card(
        backgroundColor = if (selected) Color(0xFFcae3d5) else Color.White,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { selectItem() },
        elevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Text(
                text = car.owner,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )
            Text(
                text = car.carType,
                fontSize = 14.sp,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "${car.canton}-${car.plateNumber}",
                fontSize = 14.sp,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
            )

        }

    }
}

