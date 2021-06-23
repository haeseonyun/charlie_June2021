package de.htwberlin.learningcompanion.userinterface.fragment.learning.evaluation


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.userinterface.fragment.learning.LightLevel
import de.htwberlin.learningcompanion.userinterface.fragment.learning.NoiseLevel
import de.htwberlin.learningcompanion.userinterface.fragment.learning.SessionEvaluator
import de.htwberlin.learningcompanion.userinterface.viewmodel.GoalViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.LearningSessionViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.PlaceViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import de.htwberlin.learningcompanion.util.getGoalText
import de.htwberlin.learningcompanion.util.setColorUsername
import kotlinx.android.synthetic.main.fragment_evaluate_goal_achieved.*

class EvaluateGoalAchieved : Fragment() {


    private val viewModelFactory = ViewModelFactory
    private lateinit var learningSessionViewModel: LearningSessionViewModel
    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var goalViewModel: GoalViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_evaluate_goal_achieved, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        goalViewModel = ViewModelProviders.of(this, viewModelFactory).get(GoalViewModel::class.java)
        placeViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlaceViewModel::class.java)
        learningSessionViewModel = ViewModelProviders.of(this, viewModelFactory).get(LearningSessionViewModel::class.java)

        iv_charlie.setImageDrawable(BuddyFaceHolder.get(context!!).getDefaultFace())

        setBackgroundPicture()
        setGoalText()
        addButtonClickListener()


        setColorUsername(context!!, tvRateGoal, "", ", how would you rate your goal achievement?")
    }

    private fun addButtonClickListener() {
        btn_next.setOnClickListener {

            val builder = AlertDialog.Builder(context!!)
            builder.setTitle(R.string.goaleval_suredialog_title)
            builder.setMessage(getString(R.string.goaleval_suredialog_msg, sb_user_rating.progress))


            builder.setPositiveButton(R.string.yes) { _, _ ->

                updateSessionWithUserRating()

                findNavController().navigate(EvaluateGoalAchievedDirections.actionEvaluateGoalAchievedToEvaluateSuccessFactor())
            }

            builder.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

    }

    private fun updateSessionWithUserRating() {
        learningSessionViewModel.currentLearningSession.value?.let { currentLearningSession ->
            currentLearningSession.userRating = sb_user_rating.progress
            currentLearningSession.brightnessRating = LightLevel.fromValue(SessionEvaluator.calculateAverage(currentLearningSession.lightValues))
            currentLearningSession.noiseRating = NoiseLevel.fromValue(SessionEvaluator.calculateAverage(currentLearningSession.noiseValues))

            learningSessionViewModel.repository.updateLearningSession(currentLearningSession)
        }
    }

    private fun setGoalText() {
        tv_goal_text.text = getGoalText(goalViewModel.currentGoal.value)
    }

    private fun setBackgroundPicture() {
        placeViewModel.currentPlace.value?.let { currentPlace ->
            if (currentPlace.imageUri != null) {
                val inputStream = activity!!.contentResolver.openInputStream(currentPlace.imageUri!!)
                val drawable = Drawable.createFromStream(inputStream, currentPlace.imageUri.toString())
                iv_place_background.setImageDrawable(drawable)
            }
        }
    }
}
