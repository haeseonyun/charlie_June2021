package de.htwberlin.learningcompanion.userinterface.fragment.learning

import de.htwberlin.learningcompanion.util.*

enum class LightLevel(val levelName: String) {
    LOWEST("Dark"), LOW("Dim"), MEDIUM("Medium"), HIGH("Bright"), HIGHEST("Shiny");

    companion object {
        fun fromValue(value: Double): LightLevel {
            return when (value) {
                in 0.0..LIGHT_LOW_THRESHOLD -> LOWEST
                in LIGHT_LOW_THRESHOLD..LIGHT_MEDIUM_THRESHOLD -> LOW
                in LIGHT_MEDIUM_THRESHOLD..LIGHT_HIGH_THRESHOLD -> MEDIUM
                in LIGHT_HIGH_THRESHOLD..LIGHT_HIGHEST_THRESHOLD -> HIGH
                in LIGHT_HIGHEST_THRESHOLD..Double.MAX_VALUE -> HIGHEST
                else -> {
                    LOWEST
                }
            }
        }
    }
}

enum class NoiseLevel(val levelName: String) {
    LOWEST("Silent"), LOW("Quite"), MEDIUM("Medium"), HIGH("Loud"), HIGHEST("Noisy");

    companion object {
        fun fromValue(value: Double): NoiseLevel {
            return when (value) {
                in 0.0..NOISE_LOW_THRESHOLD -> LOWEST
                in NOISE_LOW_THRESHOLD..NOISE_MEDIUM_THRESHOLD -> LOW
                in NOISE_MEDIUM_THRESHOLD..NOISE_HIGH_THRESHOLD -> MEDIUM
                in NOISE_HIGH_THRESHOLD..NOISE_HIGHEST_THRESHOLD -> HIGH
                in NOISE_HIGHEST_THRESHOLD..Double.MAX_VALUE -> HIGHEST
                else -> {
                    LOWEST
                }
            }
        }
    }
}


enum class DistractionLevel(val levelName: String) {
    LOW("Concentrated"), MEDIUM("Slightly"), HIGH("Moderate"), HIGHEST("High");

    companion object {
        fun fromValue(value: Double): DistractionLevel {
            return when (value) {
                in 0.0..DISTRATION_SLIGHTLY_THRESHOLD -> LOW
                in DISTRATION_SLIGHTLY_THRESHOLD..DISTRATION_MODERATE_THRESHOLD -> MEDIUM
                in DISTRATION_MODERATE_THRESHOLD..DISTRATION_HIGH_THRESHOLD -> HIGH
                in DISTRATION_HIGH_THRESHOLD..Double.MAX_VALUE -> HIGHEST
                else -> {
                    LOW
                }
            }
        }
    }
}

class SessionEvaluator(private val lightValues: List<Float>,
                       private val noiseValues: List<Float>) {

    private val TAG = SessionEvaluator::class.java.simpleName

    private var lightLevel: LightLevel = LightLevel.MEDIUM
    private var noiseLevel: NoiseLevel = NoiseLevel.MEDIUM

    fun evaluateLight(): LightLevel {
        if (!lightValues.isEmpty()) {
            lightLevel = LightLevel.fromValue(lightValues.last().toDouble())
        }
        return lightLevel
    }

    fun evaluateNoise(): NoiseLevel {
        if (!noiseValues.isEmpty()) {
            noiseLevel = NoiseLevel.fromValue(noiseValues.last().toDouble())
        }
        return noiseLevel
    }

    companion object {
        fun <T : Number> calculateAverage(values: List<T>): Double {
            var sum = 0.0

            values.forEach { sum = sum.plus(it.toFloat()) }

            return sum / values.size
        }
    }

}