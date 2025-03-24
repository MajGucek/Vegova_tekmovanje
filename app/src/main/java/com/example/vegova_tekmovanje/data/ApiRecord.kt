package com.example.vegova_tekmovanje.data

// Represents each record from the API response
data class ApiRecord(
    val power: Double,    // Power consumption (in watts)
    val voltage: Double,  // Voltage level (in volts)
    val timestamp: String   // Timestamp of the record
)
