package de.htwberlin.learningcompanion.buddy

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.persistance.model.Place
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.getGoalText
import java.util.*

const val MESSAGE_DURATION_IN_MILLIS = 10000L

class Buddy private constructor(private val context: Context) {

    private val buddyFaceHolder = BuddyFaceHolder.get(context)

    var isInDefaultState = true

    val drawableLiveData = MutableLiveData<Drawable>()
    val speechLiveData = MutableLiveData<String>()

    var currentGoal: Goal? = null
    var currentPlace: Place? = null

    fun setNewRandomBuddyLearningText() {
        val randomArrayIndex = Random().nextInt(6)

        when (randomArrayIndex) {
            1 -> setRandomThinkingCharlie()
            2 -> setRandomSmilingCharlie()
            3 -> setRandomGrinningCharlie()
            4 -> setRandomGoofyCharlie()
            5 -> setRandomRelievedCharlie()
        }
    }

    fun setNewRandomBuddyBeforeLearningText() {
        showMessageForFixedAmount(getInfoText(), buddyFaceHolder.getDefaultFace())
    }

    fun setInfo() {
        showMessage(getInfoText(), buddyFaceHolder.getDefaultFace())
    }

    fun setInstructionText() {
        showMessageForFixedAmount(getInfoText(), buddyFaceHolder.getDefaultFace())
    }

    fun showExitProhibitedMessage() {
        showMessageForFixedAmount(context.getString(R.string.exit_prohibited_message), buddyFaceHolder.getThinkingFace())
    }

    fun showStartLearningText() {
        showMessageForFixedAmount(context.getString(R.string.buddy_startlearning), buddyFaceHolder.getThinkingFace())
    }

    fun showKeepLearningText() {
        showMessageForFixedAmount("${SharedPreferencesHelper.get(context).getUserName()}, please focus on your goal!", buddyFaceHolder.getThinkingFace())
    }

    private fun showMessage(message: String, drawable: Drawable?) {
        speechLiveData.value = message
        drawableLiveData.value = drawable
    }

    private fun showMessageForFixedAmount(message: String, drawable: Drawable?) {
        if (isInDefaultState) {
            isInDefaultState = false
            speechLiveData.value = message
            drawableLiveData.value = drawable

            Handler().postDelayed({
                isInDefaultState = true
                speechLiveData.postValue("")
                drawableLiveData.postValue(buddyFaceHolder.getDefaultFace())
            }, MESSAGE_DURATION_IN_MILLIS)
        }
    }

    private fun setRandomThinkingCharlie() {
        val stringArray = context.resources.getStringArray(R.array.buddy_thinking_sayings)
        val randomStringIndex = Random().nextInt(stringArray.size)

        showMessageForFixedAmount(stringArray[randomStringIndex], buddyFaceHolder.getThinkingFace())
    }

    private fun setRandomSmilingCharlie() {
        val stringArray = context.resources.getStringArray(R.array.buddy_smiling_sayings)
        val randomStringIndex = Random().nextInt(stringArray.size)

        showMessageForFixedAmount(stringArray[randomStringIndex], buddyFaceHolder.getSmilingFace())
    }

    private fun setRandomGoofyCharlie() {
        val stringArray = context.resources.getStringArray(R.array.buddy_goofy_sayings)
        val randomStringIndex = Random().nextInt(stringArray.size)

        showMessageForFixedAmount(stringArray[randomStringIndex], buddyFaceHolder.getGoofyFace())
    }

    private fun setRandomGrinningCharlie() {
        val stringArray = context.resources.getStringArray(R.array.buddy_grinning_sayings)
        val randomStringIndex = Random().nextInt(stringArray.size)

        showMessageForFixedAmount(stringArray[randomStringIndex], buddyFaceHolder.getGrinningFace())
    }

    private fun setRandomRelievedCharlie() {
        val stringArray = context.resources.getStringArray(R.array.buddy_relieved_sayings)
        val randomStringIndex = Random().nextInt(stringArray.size)

        showMessageForFixedAmount(stringArray[randomStringIndex], buddyFaceHolder.getCalmFace())
    }

    fun getInfoText(formatted: Boolean = false): String {

        return when {
            currentGoal == null -> getGoalInfoText()
            currentPlace == null -> getPlaceInfoText()
            formatted == true -> getStartLearningInfoText2(currentGoal!!, currentPlace!!)
            else -> getStartLearningInfoText(currentGoal!!, currentPlace!!)
        }
    }

    private fun getGoalInfoText(): String {
        return ", please press \"Menu\" and go to \"My Goals\" to set the goal that you want to achieve."
    }

    private fun getPlaceInfoText(): String {
        return ", please press \"Menu\" and go to \"My places\" to set the place where you want to learn."
    }

    private fun getStartLearningInfoText(currentGoal: Goal, currentPlace: Place): String {
        return "Your current goal is: \n${getGoalText(currentGoal)} \nYour current place is ${currentPlace.name}"
    }

    private fun getStartLearningInfoText2(currentGoal: Goal, currentPlace: Place): String {
        return ", your current goal is: \n${getGoalText(currentGoal)} \nYour current place is ${currentPlace.name}"
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: Buddy? = null

        fun get(context: Context): Buddy = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Buddy(context).also { INSTANCE = it }
        }
    }
}
