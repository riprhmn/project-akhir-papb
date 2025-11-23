// Create file: DistanceCalculator.kt
package com.example.dashboard2.utils

import kotlin.math.*

object DistanceCalculator {

    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c
    }

    /**
     * Format distance to readable string
     */
    fun formatDistance(distanceKm: Double): String {
        return when {
            distanceKm < 1.0 -> {
                val meters = (distanceKm * 1000).roundToInt()
                "$meters m"
            }
            distanceKm < 10.0 -> {
                String.format("%.1f km", distanceKm)
            }
            else -> {
                "${distanceKm.roundToInt()} km"
            }
        }
    }
}