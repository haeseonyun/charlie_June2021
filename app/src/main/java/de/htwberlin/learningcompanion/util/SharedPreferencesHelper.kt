package de.htwberlin.learningcompanion.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.lifecycle.MutableLiveData

class SharedPreferencesHelper private constructor(context: Context) {

    private val SHARED_PREF_NAME = "preferences"

    private val DEFAULT_USER_NAME = "Buddy"
    private val DEFAULT_COMPANION_NAME = "Charlie"

    private val sharedPref: SharedPreferences

    val buddyColorLiveData = MutableLiveData<Int>()

    init {
        sharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getBuddyMood(): String {
        val charlieNum = sharedPref.getInt("MoodNumber", 0)

        return when (charlieNum) {
            1 -> "gentle"
            2 -> "nerdy"
            3 -> "calm"
            4 -> "happy"
            5 -> "goofy"
            else -> {
                "gentle"
            }
        }
    }

    fun getBuddyColor(): Int {
        val charlieNum = sharedPref.getInt("MoodNumber", 0)

        return when (charlieNum) {
            1 -> Color.BLUE
            2 -> Color.GREEN
            3 -> Color.MAGENTA
            4 -> Color.YELLOW
            5 -> Color.RED
            else -> Color.BLUE
        }
    }

    fun getCharlieNum(): Int {
        return sharedPref.getInt("MoodNumber", 0)
    }

    fun setBuddyMood(moodNumber: Int) {
        val editor = sharedPref.edit()
        editor.putInt("MoodNumber", moodNumber)
        editor.apply()

//        buddyColorLiveData.postValue(moodNumber)
        buddyColorLiveData.value = moodNumber
    }

    fun getUserName(): String {
        return sharedPref.getString("UserName", DEFAULT_USER_NAME)!!
    }

    fun setUserName(userName: String) {
        val editor = sharedPref.edit()
        editor.putString("UserName", userName)
        editor.apply()
    }

    fun getBuddyName(): String {
        return sharedPref.getString("BuddyName", DEFAULT_COMPANION_NAME)!!
    }

    fun setBuddyName(buddyName: String) {
        val editor = sharedPref.edit()
        editor.putString("BuddyName", buddyName)
        editor.apply()
    }

    fun setInterval(interval: Int) {
        val editor = sharedPref.edit()
        editor.putInt("interval", interval)
        editor.apply()
    }

    fun setFrequency(frequency: Int) {
        val editor = sharedPref.edit()
        editor.putInt("frequency", frequency)
        editor.apply()
    }

    fun getInterval() : Int {
        return sharedPref.getInt("interval", 0)
    }

    fun getFrequency() : Int {
        return sharedPref.getInt("frequency", 0)
    }

    fun setFirstStart(firstStart : Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean("firstStart", firstStart)
        editor.apply()
    }

    fun getFirstStart() : Boolean {
        return sharedPref.getBoolean("firstStart", true)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: SharedPreferencesHelper? = null

        fun get(context: Context): SharedPreferencesHelper = INSTANCE ?: synchronized(this) {
            INSTANCE ?: SharedPreferencesHelper(context).also { INSTANCE = it }
        }
    }
}