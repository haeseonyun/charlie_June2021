package de.htwberlin.learningcompanion

/**
 * Created by Max on 2019-05-27.
 *
 * @author Max Oehme
 */
const val COMM_SENSORDATA_RAW = "de.htwberlin.learningcompanion.COMM_SENSORDATA_RAW"
const val COMM_SENSORDATA_PROCESSED = "de.htwberlin.learningcompanion.COMM_SENSORDATA_PROCESSED"

const val COMM_DISTRACTION_EVENT = "de.htwberlin.learningcompanion.COMM_DISTRACTION_EVENT"

const val COMM_EXTRA_JSON = "de.htwberlin.learningcompanion.COMM_EXTRA_JSON"

fun formatDuration(duration: Long) : String {
    val seconds = Math.abs(duration / 1000)

    return when (0L) {
        (seconds % 3600) / 60 -> {
            String.format("%ds",
                    (seconds % 60)
            )
        }
        seconds / 3600 -> {
            String.format("%dm %ds",
                    (seconds % 3600) / 60,
                    (seconds % 60)
            )
        }
        else -> {
            String.format("%dh %dm %ds",
                    seconds / 3600,
                    (seconds % 3600) / 60,
                    (seconds % 60)
            )
        }
    }
}