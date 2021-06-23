package de.htwberlin.learningcompanion.persistance.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.htwberlin.learningcompanion.userinterface.fragment.learning.LightLevel
import de.htwberlin.learningcompanion.userinterface.fragment.learning.NoiseLevel
import java.util.*

@Entity(tableName = "goal")
data class Goal(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        val action: String,
        val amount: String,
        val field: String,
        val medium: String,
        var durationInMin: Int? = null,
        var untilTimeStamp: String? = null,
        var createdAtTimestamp: Long
)
