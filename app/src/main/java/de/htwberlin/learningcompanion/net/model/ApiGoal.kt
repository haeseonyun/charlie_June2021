package de.htwberlin.learningcompanion.net.model

data class ApiGoal(
    val action: String,
    val amount: String,
    val durationInMin: String,
    val `field`: String,
    val medium: String,
    val untilTimeStamp: String
)