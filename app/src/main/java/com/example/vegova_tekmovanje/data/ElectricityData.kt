package com.example.vegova_tekmovanje.data


data class ElectricityData(
    val ura: String,    // Time of the reading
    val kwh: String,    // Kilowatt-hour value
    val tarifa: String  // Tariff value
)

