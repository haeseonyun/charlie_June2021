package de.htwberlin.learningcompanion.buddy

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper

class BuddyFaceHolder private constructor(private val context: Context) {

    private val sharedPreferencesHelper = SharedPreferencesHelper.get(context)

    fun getGoofyFace(): Drawable? {
        val buddyColor = sharedPreferencesHelper.getBuddyColor()

        return when (buddyColor) {
            Color.BLUE -> context.getDrawable(R.drawable.blue_goofy)
            Color.GREEN -> context.getDrawable(R.drawable.green_goofy_glasses)
            Color.MAGENTA -> context.getDrawable(R.drawable.pink_charlie_goofy)
            Color.YELLOW -> context.getDrawable(R.drawable.yellow_charlie_goofy)
            Color.RED -> context.getDrawable(R.drawable.orange_charlie_goofy)   // yellow
            else -> context.getDrawable(R.drawable.blue_goofy)
        }
    }

    fun getCalmFace(): Drawable? {
        val buddyColor = sharedPreferencesHelper.getBuddyColor()

        return when (buddyColor) {
            Color.BLUE -> context.getDrawable(R.drawable.blue_relieved)
            Color.GREEN -> context.getDrawable(R.drawable.green_calm_glasses)
            Color.MAGENTA -> context.getDrawable(R.drawable.pink_charlie_relieved)
            Color.YELLOW -> context.getDrawable(R.drawable.yellow_charlie_relieved)
            Color.RED -> context.getDrawable(R.drawable.orange_charlie_relieved)
            else -> context.getDrawable(R.drawable.blue_relieved)
        }
    }

    fun getGrinningFace(): Drawable? {
        val buddyColor = sharedPreferencesHelper.getBuddyColor()

        return when (buddyColor) {
            Color.BLUE -> context.getDrawable(R.drawable.blue_grinning)
            Color.GREEN -> context.getDrawable(R.drawable.green_grinning_glasses)
            Color.MAGENTA -> context.getDrawable(R.drawable.pink_charlie_grinning)
            Color.YELLOW -> context.getDrawable(R.drawable.yellow_charlie_grinning)
            Color.RED -> context.getDrawable(R.drawable.orange_charlie_grinning)
            else -> context.getDrawable(R.drawable.blue_grinning)
        }
    }

    fun getSmilingFace(): Drawable? {
        val buddyColor = sharedPreferencesHelper.getBuddyColor()

        return when (buddyColor) {
            Color.BLUE -> context.getDrawable(R.drawable.blue_smile)
            Color.GREEN -> context.getDrawable(R.drawable.green_smile_glasses)
            Color.MAGENTA -> context.getDrawable(R.drawable.pink_charlie_smiling)
            Color.YELLOW -> context.getDrawable(R.drawable.yellow_charlie_smiling)
            Color.RED -> context.getDrawable(R.drawable.orange_charlie_smiling)
            else -> context.getDrawable(R.drawable.blue_smile)
        }
    }

    fun getThinkingFace(): Drawable? {
        val buddyColor = sharedPreferencesHelper.getBuddyColor()

        return when (buddyColor) {
            Color.BLUE -> context.getDrawable(R.drawable.blue_thinking)
            Color.GREEN -> context.getDrawable(R.drawable.green_thinking_glasses)
            Color.MAGENTA -> context.getDrawable(R.drawable.pink_charlie_thinking)
            Color.YELLOW -> context.getDrawable(R.drawable.yellow_charlie_thinking)
            Color.RED -> context.getDrawable(R.drawable.orange_charlie_thinking)
            else -> context.getDrawable(R.drawable.blue_thinking)
        }
    }

    fun getDefaultFace(): Drawable? {
        val buddyColor = sharedPreferencesHelper.getBuddyColor()

        return when (buddyColor) {
            Color.BLUE -> context.getDrawable(R.drawable.blue_smile)
            Color.GREEN -> context.getDrawable(R.drawable.green_smile_glasses)
            Color.MAGENTA -> context.getDrawable(R.drawable.pink_charlie_smiling)
            Color.YELLOW -> context.getDrawable(R.drawable.yellow_charlie_smiling)
            Color.RED -> context.getDrawable(R.drawable.orange_charlie_smiling)
            else -> context.getDrawable(R.drawable.blue_smile)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: BuddyFaceHolder? = null

        fun get(context: Context): BuddyFaceHolder = INSTANCE ?: synchronized(this) {
            INSTANCE ?: BuddyFaceHolder(context).also { INSTANCE = it }
        }
    }
}