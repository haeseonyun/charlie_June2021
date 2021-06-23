package de.htwberlin.learningcompanion.userinterface.fragment.recommendation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.userinterface.fragment.learning.LightLevel
import de.htwberlin.learningcompanion.userinterface.fragment.learning.NoiseLevel
import de.htwberlin.learningcompanion.userinterface.viewmodel.LearningSessionViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import de.htwberlin.learningcompanion.util.getGoalText
import de.htwberlin.learningcompanion.util.setActivityTitle
import de.htwberlin.learningcompanion.util.setColorUsername
import kotlinx.android.synthetic.main.fragment_recommendation.*
import org.jetbrains.anko.support.v4.runOnUiThread

class RecommendationFragment : Fragment() {

    private val viewModelFactory = ViewModelFactory
    private lateinit var recommendationHelper: RecommendationViewModel
    private lateinit var learningSessionViewModel: LearningSessionViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommendation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setActivityTitle(getString(R.string.title_nav_menu_recommendation))


        recommendationHelper = ViewModelProviders.of(this, viewModelFactory).get(RecommendationViewModel::class.java)
        learningSessionViewModel = ViewModelProviders.of(this, viewModelFactory).get(LearningSessionViewModel::class.java)
        learningSessionViewModel.learningSessions.observe(this, Observer {
            if (it.isEmpty()) {

                setColorUsername(context!!, tv_charlie_info, "", ", please finish some learning sessions to view your recommendations.")
            } else {
                recommendationHelper.calculateRecommendation {
                    runOnUiThread {
                        fillLayout()
                    }
                }
            }
        })

        iv_charlie.setImageDrawable(BuddyFaceHolder.get(context!!).getDefaultFace())

    }

    private fun fillLayout() {
        setBestGoals()
        setBestTime()
        setBestDuration()
        setBestPlaces()
        setBestBrightness()
        setBestNoise()
        setTop3SuccessIssues()
    }

    private fun setBestGoals() {
        val bestGoals = recommendationHelper.bestGoals
        if (bestGoals != null) {
            if (bestGoals.size > 0) {
                tv_best_goal_1.text = getGoalText(bestGoals[0])
//                tv_best_goal_1_percent.text = "${LearningSessionRepository.get(context!!).getLearningSessionsByGoalID(bestGoals[0].id)[0].userRating} %"
            }
            if (bestGoals.size > 1) {
                tv_best_goal_2.text = getGoalText(bestGoals[1])
//                tv_best_goal_2_percent.text = "${LearningSessionRepository.get(context!!).getLearningSessionsByGoalID(bestGoals[1].id)[0].userRating} %"
            }
            if (bestGoals.size > 2) {
                tv_best_goal_3.text = getGoalText(bestGoals[2])
//                tv_best_goal_3_percent.text = "${LearningSessionRepository.get(context!!).getLearningSessionsByGoalID(bestGoals[2].id)[0].userRating} %"
            }
        }

    }

    private fun setBestPlaces() {
        val bestPlaces = recommendationHelper.bestPlaces
        if (bestPlaces != null) {
            if (bestPlaces.isNotEmpty()) {
                tv_place1.text = bestPlaces[0].name
                tv_address1.text = bestPlaces[0].addressString
            }
            if (bestPlaces.size > 1) {
                tv_place2.text = bestPlaces[1].name
                tv_address2.text = bestPlaces[1].addressString
            }
        }
    }

    private fun setBestDuration() {
        val bestDuration = recommendationHelper.bestDuration
        if (bestDuration != 0)
            tv_best_duration_value.text = "$bestDuration minutes"
    }

    private fun setBestTime() {
        val bestTime = recommendationHelper.bestTime
        tv_best_time_value.text = bestTime
    }

    private fun setBestBrightness() {
        val bestBrightnessValue = recommendationHelper.bestBrightnessValue
        tv_brightness_value.text = "$bestBrightnessValue lux"
        tv_brightness_level.text = LightLevel.fromValue(bestBrightnessValue.toDouble()).levelName

    }

    private fun setBestNoise() {
        val bestNoiseValue = recommendationHelper.bestNoiseValue
        tv_noise_value.text = "$bestNoiseValue"
        tv_noise_level.text = NoiseLevel.fromValue(bestNoiseValue.toDouble()).levelName

    }

    private fun setTop3SuccessIssues() {
        val success = recommendationHelper.topSuccessFactor

        for (i in 0 until success.size) {
            when (i) {
                0 -> tvTopSuccess1.text = success.component1()
                1 -> tvTopSuccess2.text = success.component2()
                2 -> tvTopSuccess3.text = success.component3()
            }
        }

        val issues = recommendationHelper.topIssues
        for (i in 0 until issues.size) {
            when (i) {
                0 -> tvTopIssues1.text = issues.component1()
                1 -> tvTopIssues2.text = issues.component2()
                2 -> tvTopIssues3.text = issues.component3()
            }
        }

    }
}
