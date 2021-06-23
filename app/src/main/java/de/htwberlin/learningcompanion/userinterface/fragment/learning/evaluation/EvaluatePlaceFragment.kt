package de.htwberlin.learningcompanion.userinterface.fragment.learning.evaluation


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.userinterface.fragment.learning.SessionEvaluator
import de.htwberlin.learningcompanion.userinterface.fragment.learning.session.SessionFragment
import de.htwberlin.learningcompanion.userinterface.service.LearningSeassionService.Companion.FINISHED_NOTIFY_ID
import de.htwberlin.learningcompanion.userinterface.viewmodel.GoalViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.LearningSessionViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.PlaceViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import de.htwberlin.learningcompanion.util.setColorUsername
import kotlinx.android.synthetic.main.fragment_evaluate_goal_achieved.*
import kotlinx.android.synthetic.main.fragment_evaluate_place.*
import kotlinx.android.synthetic.main.fragment_evaluate_place.btn_next
import kotlinx.android.synthetic.main.fragment_evaluate_place.iv_charlie
import kotlinx.android.synthetic.main.fragment_evaluate_place.iv_place_background
import kotlinx.android.synthetic.main.fragment_evaluate_success_factor.*
import kotlinx.android.synthetic.main.fragment_session.*
import kotlinx.android.synthetic.main.session_list_item.*
import java.util.*
import kotlin.math.absoluteValue

class EvaluatePlaceFragment : Fragment() {
    /*DATABASE*/
    //val addUrl : String = "http://192.168.178.20/companion/addRunGoal.php"
    val addUrl : String = "http://192.168.178.32/charlie/addRunGoal.php"
    //val addUrl : String = "http://companion.hu-informatik.berlin.de/addRunGoal.php"
    private val viewModelFactory = ViewModelFactory
    private lateinit var learningSessionViewModel: LearningSessionViewModel
    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var goalViewModel: GoalViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_evaluate_place, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        goalViewModel = ViewModelProviders.of(this, viewModelFactory).get(GoalViewModel::class.java)
        placeViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlaceViewModel::class.java)
        learningSessionViewModel = ViewModelProviders.of(this, viewModelFactory).get(LearningSessionViewModel::class.java)

        iv_charlie.setImageDrawable(BuddyFaceHolder.get(context!!).getDefaultFace())

        setBackgroundPicture()
        setPlaceText()
        addButtonClickListener()

        setColorUsername(context!!, tv_noise_header_text, "", getString(R.string.goaleval_place_noisetext_enable))
        setColorUsername(context!!, tv_brightness_header_text, "", getString(R.string.goaleval_place_brighttext_enable))

        if (permissionsGranted()) {
            // means that we collected the values and the user should not choose them
            enableInputFields(false)
        } else {
            enableInputFields(true)
        }
    }

    private fun enableInputFields(enable: Boolean) {
        sb_noise_rating.setUserSeekAble(enable)
        sb_brightness_rating.setUserSeekAble(enable)

        learningSessionViewModel.currentLearningSession.value?.let { currentLearningSession ->
            if (enable) {
                sb_noise_rating.setProgress(currentLearningSession.noiseRating.ordinal.toFloat())
                sb_brightness_rating.setProgress(currentLearningSession.brightnessRating.ordinal.toFloat())
            } else {
                tv_noise_header_text.text = getString(R.string.goaleval_place_noisetext_disable)
                tv_brightness_header_text.text = getString(R.string.goaleval_place_brighttext_disable)

                sb_noise_rating.setProgress(currentLearningSession.noiseRating.ordinal.toFloat())
                sb_brightness_rating.setProgress(currentLearningSession.brightnessRating.ordinal.toFloat())
            }
        }
    }

    private fun permissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun addButtonClickListener() {
        btn_next.setOnClickListener {
            /*DATABASE*/
            addData()
            goalViewModel.currentGoal.value = null
            placeViewModel.currentPlace.value = null
            NotificationManagerCompat.from(context!!).cancel(FINISHED_NOTIFY_ID)

            findNavController().navigate(
                    EvaluatePlaceFragmentDirections.actionEvaluatePlaceFragmentToNavgraphSurveyEvaluate()
            )

            /*
            findNavController().navigate(
                    MainScreenFragmentDirections.actionGlobalSessionOverviewFragment()
                )
            */
        }
    }

    private fun setPlaceText() {
        placeViewModel.currentPlace.value?.let { place ->
            val name = place.name
            val address = place.addressString
            if (address == null || address == "") {
                tv_place_text.text = "$name"
            } else {
                tv_place_text.text = "$name \n$address"
            }
        }
    }

    private fun setBackgroundPicture() {
        placeViewModel.currentPlace.value?.let { currentPlace ->
            if (currentPlace.imageUri != null) {
                val inputStream = activity!!.contentResolver.openInputStream(currentPlace.imageUri)
                val drawable = Drawable.createFromStream(inputStream, currentPlace.imageUri.toString())
                iv_place_background.setImageDrawable(drawable)
            }
        }
    }
    /*DATABASE*/
    fun addData() {
        val getID = Settings.Secure.getString(context?.getContentResolver(), Settings.Secure.ANDROID_ID)
        val getGoalId = goalViewModel.currentGoal.value?.id.toString()
        val getTime = Calendar.getInstance().time.toString()
        val getAction = goalViewModel.currentGoal.value?.action.toString() +", " + goalViewModel.currentGoal.value?.field.toString() + ", " + goalViewModel.currentGoal.value?.amount.toString() + ", " + goalViewModel.currentGoal.value?.medium.toString() + ", "
        val getCurrentPlace = tv_place_text.text.toString()
        val getAverageLight = learningSessionViewModel.currentLearningSession.value?.brightnessRating.toString()
        val getAverageNoise = learningSessionViewModel.currentLearningSession.value?.noiseRating.toString()
        val getDuration = goalViewModel.currentGoal.value?.durationInMin.toString() + " min"
        val getDistraction = SessionEvaluator.calculateAverage(learningSessionViewModel.currentLearningSession.value!!.distractionLevel).toFloat().toString()
        val getGoalAchievment = learningSessionViewModel.currentLearningSession.value?.userRating.toString() + "%"
        val getEvaluation = learningSessionViewModel.currentLearningSession.value?.evaluationSuccessFactor.toString()

        val postRequest: StringRequest = object : StringRequest(Method.POST, addUrl,
                Response.Listener { response -> // response
                    Log.d("Response", response)
                },
                Response.ErrorListener { response ->// error
                    Log.d("Error.Response", response.toString())
                }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params.put("id", getID)
                params.put("goal_id", getGoalId)
                params.put("time", getTime)
                params.put("action", getAction )
                params.put("current_place", getCurrentPlace)
                params.put("average_light", getAverageLight)
                params.put("average_noise", getAverageNoise)
                params.put("duration", getDuration)
                params.put("distraction", getDistraction)
                params.put("goal_achievment", getGoalAchievment)
                params.put("evaluation", getEvaluation)
                return params
            }
        }
        val queue = Volley.newRequestQueue(context!!)
        queue.add(postRequest)
    }
}
