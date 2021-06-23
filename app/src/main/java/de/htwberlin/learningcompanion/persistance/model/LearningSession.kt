package de.htwberlin.learningcompanion.persistance.model

import android.util.SparseArray
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import de.htwberlin.learningcompanion.userinterface.fragment.learning.LightLevel
import de.htwberlin.learningcompanion.userinterface.fragment.learning.NoiseLevel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Entity(tableName = "session", foreignKeys = [
    (ForeignKey(entity = Goal::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("goal_id"),
            onDelete = CASCADE)),
    (ForeignKey(entity = Place::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("place_id"),
            onDelete = CASCADE))])
data class LearningSession(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        @ColumnInfo(name = "place_id") var placeID: Long = 0,
        @ColumnInfo(name = "goal_id") var goalID: Long = 0,
        var createdAtTimestamp: Long = 0,
        var studyDuration: Long = 0,


        var userRating: Int? = null,
        var evaluationSuccessFactor: String? = null,
        var noiseValues: List<Float> = arrayListOf(),
        var lightValues: List<Float> = arrayListOf(),
        var distractionLevel: List<Int> = arrayListOf(0),
        val movementStates: HashMap<Long, String> = HashMap(),
        val orientationStates: HashMap<Long, String> = HashMap(),
        val faceTapped: List<Long> = arrayListOf(),
        val events: HashMap<Long, String> = HashMap(),

        var noiseRating: NoiseLevel = NoiseLevel.MEDIUM,
        var brightnessRating: LightLevel = LightLevel.MEDIUM
)
