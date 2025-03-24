package com.example.vegova_tekmovanje.ui

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.example.vegova_tekmovanje.data.ElectricityData
import kotlinx.coroutines.delay
import com.example.vegova_tekmovanje.data.TimeData
import com.example.vegova_tekmovanje.network.RetrofitClient
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.view.LineChartView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElectricityDataScreen()
        }
    }
}

@Composable
fun PowerChart(timeData: List<TimeData>, electricityData: List<ElectricityData>) {
    // Calculate dynamic min/max values for each metric
    val mocMin = timeData.minOf { it.moc.toFloatOrNull() ?: 0f }
    val mocMax = timeData.maxOf { it.moc.toFloatOrNull() ?: 0f }

    val tokMin = timeData.minOf { it.tok.toFloatOrNull() ?: 0f }
    val tokMax = timeData.maxOf { it.tok.toFloatOrNull() ?: 0f }

    val napetostMin = timeData.minOf { it.napetost.toFloatOrNull() ?: 0f }
    val napetostMax = timeData.maxOf { it.napetost.toFloatOrNull() ?: 0f }

    val kwhMin = electricityData.minOf { it.kwh.toFloatOrNull() ?: 0f }
    val kwhMax = electricityData.maxOf { it.kwh.toFloatOrNull() ?: 0f }

    val tarifaMin = electricityData.minOf { it.tarifa.toFloatOrNull() ?: 0f }
    val tarifaMax = electricityData.maxOf { it.tarifa.toFloatOrNull() ?: 0f }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        item {
            // Chart for Moc (Power)
            PowerChartForMetric(
                title = "Moč [W]",
                data = timeData.mapIndexed { index, data ->
                    val mocValue = data.moc.toFloatOrNull() ?: 0f
                    PointValue((timeData.size - 1 - index).toFloat(), mocValue)
                },
                color = Color.Red,
                minValue = mocMin,
                maxValue = mocMax
            )
        }

        item {
            // Chart for Tok (Current)
            PowerChartForMetric(
                title = "Tok [I]",
                data = timeData.mapIndexed { index, data ->
                    val tokValue = data.tok.toFloatOrNull() ?: 0f
                    PointValue((timeData.size - 1 - index).toFloat(), tokValue)
                },
                color = Color.Green,
                minValue = tokMin,
                maxValue = tokMax
            )
        }

        item {
            // Chart for Napetost (Voltage)
            PowerChartForMetric(
                title = "Napetost [V])",
                data = timeData.mapIndexed { index, data ->
                    val napetostValue = data.napetost.toFloatOrNull() ?: 0f
                    PointValue((timeData.size - 1 - index).toFloat(), napetostValue)
                },
                color = Color.Blue,
                minValue = napetostMin,
                maxValue = napetostMax
            )
        }

        item {
            // Chart for kWh (Electricity Consumption)
            PowerChartForMetric(
                title = "Poraba [kWh]",
                data = electricityData.mapIndexed { index, data ->
                    val kwhValue = data.kwh.toFloatOrNull() ?: 0f
                    PointValue((electricityData.size - 1 - index).toFloat(), kwhValue)
                },
                color = Color.Yellow,
                minValue = kwhMin,
                maxValue = kwhMax
            )
        }

        item {
            // Chart for Tarifa (Tariff)
            PowerChartForMetric(
                title = "Tarifa [€]",
                data = electricityData.mapIndexed { index, data ->
                    val tarifaValue = data.tarifa.toFloatOrNull() ?: 0f
                    PointValue((electricityData.size - 1 - index).toFloat(), tarifaValue)
                },
                color = Color.Cyan,
                minValue = tarifaMin,
                maxValue = tarifaMax
            )
        }
    }
}


@Composable
fun PowerChartForMetric(
    title: String,
    data: List<PointValue>,
    color: Color,
    minValue: Float,
    maxValue: Float
) {
    val context = LocalContext.current
    val lineChartView = remember(context) { LineChartView(context) }

    val line = Line(data).apply {
        setColor(color.toArgb())
        setCubic(true)
    }

    val lineChartData = LineChartData().apply {
        lines = listOf(line)

        // Set up Y-Axis with custom min and max values
        val axis = Axis().apply {
            setHasLines(true)
            setMaxLabelChars(4) // Set number of characters per label
            var axisFrom = minValue // Set the min value
            var axisTo = maxValue // Set the max value

            // You can adjust the number of labels by defining the step size
            val step = (maxValue - minValue) / 5
            val labels = mutableListOf<String>()
            for (i in 0..5) {
                labels.add((minValue + i * step).toString())
            }

            setValues(labels.map { AxisValue(it.toFloat()) })
        }

        // Add the Y-axis to the chart
        var axisY = axis
    }

    lineChartView.lineChartData = lineChartData

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(text = title, modifier = Modifier.padding(bottom = 8.dp))

        // Display the graph with additional labels
        AndroidView(
            factory = { lineChartView },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)  // Adjust height as needed
                .border(2.dp, Color.Gray)
        )
    }
}





@Composable
fun ElectricityDataScreen() {
    val timeData = remember { mutableStateOf<List<TimeData>>(emptyList()) }
    val electricityData = remember { mutableStateOf<List<ElectricityData>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val hours = remember { mutableStateOf(3f) }

    LaunchedEffect(hours.value) {
        while (true) {
            try {
                // Fetch latest time data (with moc, tok, napetost)
                val newTimeData = RetrofitClient.apiService.getTime(hours.value.toInt())

                // Fetch latest Electricity data separately (which contains both kWh and tarifa)
                val newElectricityData = RetrofitClient.apiService.getKWH(hours.value.toInt())

                if (newTimeData.isNotEmpty() && newElectricityData.isNotEmpty()) {
                    timeData.value = newTimeData
                    electricityData.value = newElectricityData
                }

                Log.d("API", "Updated API Data: $newTimeData, $newElectricityData")
                isLoading.value = false
            } catch (e: Exception) {
                Log.e("API", "API Error: ${e.message}")
                isLoading.value = false
            }
            delay(500)  // Fetch data every 500ms
        }
    }

    Column(modifier = Modifier.padding(30.dp)) {
        Text(text = "Vtičnica Mobilna aplikacija", modifier = Modifier.padding(bottom = 20.dp))

        Slider(
            value = hours.value,
            onValueChange = { hours.value = it },
            valueRange = 3f..24f,
            steps = 21,
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Čas v preteklost: ${hours.value.toInt()} min")

        if (isLoading.value) {
            Text(text = "Loading...")
        } else {
            PowerChart(timeData.value, electricityData.value)
        }
    }
}


@Composable
fun displayDummyData() {
    /*
    // Example of dummy TimeData
    val dummyData = listOf(
        TimeData(
            id_zapisa = "1",
            stanje = "Active",
            napetost = "230",
            frekvenca = "50",
            tok = "10",
            moc = "200",  // Power value for the graph
            kwh = "0.5",  // Dummy kWh value
            tarifa = "Low",  // Dummy Tarifa value
            casovni_zig = "2023-03-23 12:00:00",
            tarifa = "Low",
            valuta = "EUR",
            casovni_block = "Block1"
        ),
        TimeData(
            id_zapisa = "2",
            stanje = "Active",
            napetost = "230",
            frekvenca = "50",
            tok = "12",
            moc = "250",
            kwh = "0.7",
            tarifa = "Low",
            casovni_zig = "2023-03-23 13:00:00",
            tarifa = "Low",
            valuta = "EUR",
            casovni_block = "Block1"
        ),
        TimeData(
            id_zapisa = "3",
            stanje = "Active",
            napetost = "230",
            frekvenca = "50",
            tok = "15",
            moc = "300",
            kwh = "1.0",
            tarifa = "Low",
            casovni_zig = "2023-03-23 14:00:00",
            tarifa = "Low",
            valuta = "EUR",
            casovni_block = "Block1"
        )
    )

    // Display the chart for dummy TimeData
    PowerChart(dummyData, dummyData, dummyData)
    */
}
